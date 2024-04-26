package flo.no.kanji.business.service.impl;

import com.moji4j.MojiDetector;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.TranslationService;
import flo.no.kanji.business.service.UserService;
import flo.no.kanji.business.service.WordService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.integration.specification.WordSpecification;
import flo.no.kanji.util.AuthUtils;
import flo.no.kanji.util.CharacterUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static flo.no.kanji.util.TranslationUtils.getExistingTranslation;

/**
 * Word business service implementation
 *
 * @author Florian
 * @see WordService
 */
@Service
@Validated
public class WordServiceImpl implements WordService {

    @Autowired
    private UserService userService;

    /** Kanji operations business service */
    @Autowired
    private KanjiService kanjiService;

    /** Words JPA repository */
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private KanjiRepository kanjiRepository;

    /** Word business/entity object mapper **/
    @Autowired
    private WordMapper wordMapper;

    /** Kanji business/entity object mapper **/
    @Autowired
    private KanjiMapper kanjiMapper;

    @Autowired
    private MojiDetector mojiDetector;

    @Value("${kanji.translation.auto.enable}")
    private Boolean enableAutoDefaultTranslation;

    /** Translation service **/
    @Autowired
    private TranslationService translationService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Word addWord(@Valid Word word, boolean preview) {

        // The word can't be already present in DB in order to be added
        checkWordAlreadyPresent(word);

        // Initializing kanjis composing the word
        if (CollectionUtils.isEmpty(word.getKanjis())) {
            word.setKanjis(this.buildWordKanjisList(word.getValue()));
        }

        // Add default furigana value
        if (word.getFuriganaValue() == null && mojiDetector.hasKanji(word.getValue())) {
            word.setFuriganaValue(CharacterUtils.getWordFurigana(word.getValue()));
        }

        // Word kanjis entities
        var wordKanjiEntities = getExistingKanjisFromWord(word);
        var kanjisToFetch = getKanjisToFetch(word.getKanjis(), wordKanjiEntities);

        // Async tasks
        var wordTranslationFutures = buildTranslationFutures(word);
        var kanjiFutures = buildKanjiFutures(kanjisToFetch);

        // Register all kanjis to save or merge with the word
        wordKanjiEntities.addAll(kanjiFutures.stream()
                .map(CompletableFuture::join)
                .map(kanjiMapper::toEntity)
                .toList());
        // Getting all the translations
        var translationsMap = wordTranslationFutures.entrySet()
                .stream()
                .filter(e -> e.getValue().join() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().join()));
        word.setTranslations(translationsMap);

        // Return created word
        if (preview) {
            word.setKanjis(wordKanjiEntities.stream().map(kanjiMapper::toBusinessObject).toList());
            return word;
        }
        return saveWord(word, wordKanjiEntities);
    }

    private Word saveWord(final Word word, List<KanjiEntity> wordKanjis) {
        var user = userService.getCurrentUser();
        var entity = wordMapper.toEntity(word);
        entity.setKanjis(wordKanjis);
        entity.setUser(user);
        return wordMapper.toBusinessObject(wordRepository.save(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Word> getWords(String search, Language language, Integer listLimit, Pageable pageable) {
        var sub = AuthUtils.getUserSub();
        return ObjectUtils.isEmpty(search)
                ? wordRepository.findAllByUserSubOrderByTimeStampDesc(sub, pageable)
                .map(w -> wordMapper.toBusinessObject(w, listLimit))
                : this.searchWord(search, pageable);
    }

    private List<CompletableFuture<Kanji>> buildKanjiFutures(List<Kanji> kanjis) {
        return kanjis.stream()
                .map(kanji -> CompletableFuture.supplyAsync(() -> {
                    kanjiService.autoFillKanjiReadigs(kanji);
                    var translations = kanjiService.buildTranslations(kanji);
                    kanji.setTranslations(translations);
                    return kanji;
                }))
                .toList();
    }

    private List<Kanji> getKanjisToFetch(List<Kanji> kanjis, List<KanjiEntity> filter) {
        return kanjis
                .stream()
                .filter(k -> filter.stream()
                        .map(KanjiEntity::getValue)
                        .noneMatch(ke -> ke.equals(k.getValue())))
                .toList();
    }

    private List<KanjiEntity> getExistingKanjisFromWord(Word word) {
        return Collections.synchronizedList(
                new ArrayList<>(kanjiRepository.findByValueIn(word.getKanjis()
                        .stream()
                        .map(Kanji::getValue)
                        .toList())));
    }

    private Map<Language, CompletableFuture<List<String>>> buildTranslationFutures(Word word) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang != Language.JA)
                .collect(Collectors.toMap(
                        Function.identity(),
                        lang -> {
                            var existingTranslation = getExistingTranslation(word.getTranslations(), lang);
                            return enableAutoDefaultTranslation && CollectionUtils.isEmpty(existingTranslation) ?
                                    fetchAutoTranslationAsync(word, lang) :
                                    CompletableFuture.completedFuture(existingTranslation);
                        }));
    }

    private CompletableFuture<List<String>> fetchAutoTranslationAsync(Word word, Language lang) {
        return CompletableFuture.supplyAsync(() -> {
            var autoTranslation = translationService.translateValue(word.getValue(), lang);
            return Optional.ofNullable(autoTranslation).map(List::of).orElse(Collections.emptyList());
        });
    }

    private void checkWordAlreadyPresent(final Word word) {
        var sub = AuthUtils.getUserSub();
        Optional.ofNullable(wordRepository.findByValueAndUserSub(word.getValue(), sub)).ifPresent(k -> {
            throw new InvalidInputException(
                    String.format("Word with value '%s' already exists in database", k.getValue()));
        });
    }

    /**
     * Builds a kanji list composing a word
     *
     * @param wordValue Word japanese writing value
     * @return List of kanji business objects composing the word
     */
    private List<Kanji> buildWordKanjisList(String wordValue) {
        return wordValue.chars().mapToObj(i -> String.valueOf((char) i)).filter(CharacterUtils::isKanji)
                .map(Kanji::new).distinct().collect(Collectors.toList());
    }

    /**
     * Perform a word search based on the japanese writing value
     *
     * @param search   Word's japanese writing value
     * @param pageable Spring pageable request properties
     * @return Spring page of retrieved corresponding words
     */
    private Page<Word> searchWord(String search, Pageable pageable) {

        var spec = WordSpecification.searchWord(search);
        return wordRepository.findAll(spec, pageable).map(wordMapper::toBusinessObject);
    }

}
