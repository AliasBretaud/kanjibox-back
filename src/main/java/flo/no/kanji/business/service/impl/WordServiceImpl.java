package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.moji4j.MojiConverter;
import com.moji4j.MojiDetector;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
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
import flo.no.kanji.util.CharacterUtils;
import flo.no.kanji.util.PatchHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import flo.no.kanji.util.ListUtils;
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
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final UserService userService;

    /**
     * Kanji operations business service
     */
    private final KanjiService kanjiService;

    /**
     * Words JPA repository
     */
    private final WordRepository wordRepository;

    private final KanjiRepository kanjiRepository;

    /**
     * Word business/entity object mapper
     **/
    private final WordMapper wordMapper;

    /**
     * Kanji business/entity object mapper
     **/
    private final KanjiMapper kanjiMapper;

    private final MojiDetector mojiDetector;

    private final MojiConverter mojiConverter;

    /**
     * Translation service
     **/
    private final TranslationService translationService;

    /**
     * Word updating fields class helper
     */
    private final PatchHelper patchHelper;

    @Value("${kanji.translation.auto.enable}")
    private Boolean enableAutoDefaultTranslation;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Word addWord(@Valid Word word, boolean preview, String userSub) {

        // The word can't be already present in DB in order to be added
        checkWordAlreadyPresent(word, userSub);

        // Initializing kanjis composing the word
        if (CollectionUtils.isEmpty(word.getKanjis())) {
            word.setKanjis(this.buildWordKanjisList(word.getValue()));
        }

        // Add default furigana value
        if (word.getFuriganaValue() == null && mojiDetector.hasKanji(word.getValue())) {
            word.setFuriganaValue(CharacterUtils.getWordFurigana(word.getValue()));
        }

        // Word kanjis entities
        var wordKanjiEntities = getExistingKanjisFromWord(word, userSub);
        var kanjisToFetch = getKanjisToFetch(word.getKanjis(), wordKanjiEntities);

        // Async tasks
        var wordTranslationFutures = buildTranslationFutures(word);
        var kanjiFutures = buildKanjiFutures(kanjisToFetch);

        // Register all kanjis to save or merge with the word
        wordKanjiEntities.addAll(kanjiFutures.stream()
                .map(CompletableFuture::join)
                .map(kanjiMapper::toEntity)
                .peek(k -> k.setUser(userService.createOrGetBySub(userSub)))
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
        return saveWord(word, wordKanjiEntities, userSub);
    }

    private Word saveWord(final Word word, List<KanjiEntity> wordKanjis, String userSub) {
        var user = userService.createOrGetBySub(userSub);
        var entity = wordMapper.toEntity(word);
        if (!CollectionUtils.isEmpty(wordKanjis)) {
            wordKanjis.forEach(k -> k.setUser(user));
        }
        entity.setKanjis(wordKanjis);
        entity.setUser(user);
        return wordMapper.toBusinessObject(wordRepository.save(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteWord(Long wordId, String userSub) {
        var entity = wordRepository.findByIdAndUserSub(wordId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Word with ID " + wordId + " not found"));
        wordRepository.delete(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Word patchWord(Long wordId, JsonNode patch, String userSub) {
        var initialEntity = wordRepository.findByIdAndUserSub(wordId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Word with ID " + wordId + " not found"));
        var initialWord = wordMapper.toBusinessObject(initialEntity);

        var patchedWord = patchHelper.mergePatch(initialWord, patch, Word.class);

        // Prevent ID update
        if (!Objects.equals(patchedWord.getId(), wordId)) {
            throw new InvalidInputException("ID update is forbidden");
        }

        return saveWord(patchedWord, initialEntity.getKanjis(), userSub);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Word> getWords(String search, Language language, Integer listLimit, Pageable pageable, String userSub) {

        Page<Word> result;
        if (ObjectUtils.isEmpty(search)) {
            result = wordRepository.findAllByUserSubOrderByTimeStampDesc(userSub, pageable)
                    .map(wordMapper::toBusinessObject);
        } else {
            result = this.searchWord(search, language, listLimit, pageable, userSub);
        }

        if (listLimit != null && listLimit > 0) {
            result.forEach(w -> {
                if (w.getTranslations() != null) {
                    w.setTranslations(w.getTranslations().entrySet().stream()
                            .collect(Collectors.toMap(e -> e.getKey(), e -> ListUtils.truncateList(e.getValue(), listLimit))));
                }
            });
        }
        return result;
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

    private List<KanjiEntity> getExistingKanjisFromWord(Word word, String userSub) {
        return Collections.synchronizedList(
                new ArrayList<>(kanjiRepository.findByValueInAndUserSub(word.getKanjis()
                        .stream()
                        .map(Kanji::getValue)
                        .toList(), userSub)));
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
        return CompletableFuture.supplyAsync(() ->
            translationService.translateValue(word.getValue(), lang)
                    .map(List::of)
                    .orElse(Collections.emptyList())
        );
    }


    private void checkWordAlreadyPresent(final Word word, String userSub) {
        wordRepository.findByValueAndUserSub(word.getValue(), userSub)
                .ifPresent(w -> {
                    throw new InvalidInputException(
                            String.format("Word with value '%s' already exists in database", w.getValue()));
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
    private Page<Word> searchWord(String search, Language language, Integer listLimit, Pageable pageable, String userSub) {
        var spec = WordSpecification.searchWord(search, mojiConverter, userSub);
        return wordRepository.findAll(spec, pageable).map(wordMapper::toBusinessObject);
    }
}
