package flo.no.kanji.business.service.impl;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.TranslationService;
import flo.no.kanji.business.service.WordService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.integration.specification.WordSpecification;
import flo.no.kanji.util.CharacterUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
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
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Word business service implementation
 *
 * @author Florian
 * @see WordService
 */
@Service
@Validated
@Slf4j
public class WordServiceImpl implements WordService {

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

    /** Japanese alphabets converting service **/
    @Autowired
    private MojiConverter converter;

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
    public Word addWord(@Valid Word word) {

        var gWatch = new StopWatch();
        gWatch.start();
        var watch = new StopWatch();
        watch.start();
        // The word can't be already present in DB in order to be added
        checkWordAlreadyPresent(word);
        watch.stop();
        log.info("checkWordAlreadyPresent - {}", watch.getTime(TimeUnit.MILLISECONDS));
        watch.reset();

        watch.start();
        // Initializing kanjis composing the word
        if (CollectionUtils.isEmpty(word.getKanjis())) {
            word.setKanjis(this.buildWordKanjisList(word.getValue()));
        }
        watch.stop();
        log.info("initialize kanjis - {}", watch.getTime(TimeUnit.MILLISECONDS));
        watch.reset();

        watch.start();
        // Word kanjis entities
        var wordKanjiEntities = getWordKanjiEntities(word);
        watch.stop();
        log.info("getWordKanjiEntities - {}", watch.getTime(TimeUnit.MILLISECONDS));
        watch.reset();

        watch.start();
        log.info("START ASYNC");
        // Async tasks
        var wordTranslationFuture = CompletableFuture.supplyAsync(() -> buildInitializedTranslations(word));
        var kanjisToFetch = getKanjisToFetch(word.getKanjis(), wordKanjiEntities);
        var kanjiFutures = buildKanjiFutures(kanjisToFetch);

        CompletableFuture.allOf(kanjiFutures.toArray(new CompletableFuture[0])).thenRun(() -> {
            log.info("READINGS API TIME - {}", watch.getTime(TimeUnit.MILLISECONDS));
        });
        wordTranslationFuture.thenRun(() -> {
            log.info("TRANSLATION API TIME - {}", watch.getTime(TimeUnit.MILLISECONDS));
        });

        // Register all kanjis to save or merge with the word
        wordKanjiEntities.addAll(kanjiFutures.stream()
                .map(CompletableFuture::join)
                .map(kanjiMapper::toEntity)
                .toList());
        word.setTranslations(wordTranslationFuture.join());

        watch.stop();
        log.info("async tasks - {}", watch.getTime(TimeUnit.MILLISECONDS));
        watch.reset();

        watch.start();
        // Build entity
        var wordEntity = wordMapper.toEntity(word);
        wordEntity.setKanjis(wordKanjiEntities);

        // Saving and return created word
        wordEntity = wordRepository.save(wordEntity);
        watch.stop();
        gWatch.stop();
        log.info("build and save - {}", watch.getTime(TimeUnit.MILLISECONDS));
        log.info("GLOBAL TIME - {}", gWatch.getTime(TimeUnit.MILLISECONDS));
        return wordMapper.toBusinessObject(wordEntity);
    }

    private List<CompletableFuture<Kanji>> buildKanjiFutures(List<Kanji> kanjis) {
        return kanjis.stream()
                .map(k -> CompletableFuture.supplyAsync(() -> {
                    kanjiService.autoFillKanjiReadigs(k);
                    return k;
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

    private List<KanjiEntity> getWordKanjiEntities(Word word) {
        return Collections.synchronizedList(
                new ArrayList<>(kanjiRepository.findByValueIn(word.getKanjis()
                        .stream()
                        .map(Kanji::getValue)
                        .toList())));
    }

    private Map<Language, List<String>> buildInitializedTranslations(Word word) {
        var translations = new HashMap<>(word.getTranslations());
        // Check if the word contains at least one default translation
        if (enableAutoDefaultTranslation && !hasDefaultTranslation(word)) {
            var defaultTranslation = translationService.translateValue(word.getValue(), Language.EN);
            if (defaultTranslation != null) {
                translations.put(Language.EN, List.of(defaultTranslation));
            }
        }
        return translations;
    }

    private boolean hasDefaultTranslation(Word word) {
        var translations = word.getTranslations();
        return translations != null && translations.containsKey(Language.EN);
    }

    private void checkWordAlreadyPresent(final Word word) {
        Optional.ofNullable(wordRepository.findByValue(word.getValue())).ifPresent(k -> {
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
                .map(Kanji::new).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Word> getWords(String search, Language language, Pageable pageable) {

        return ObjectUtils.isEmpty(search)
                ? wordRepository.findAllByOrderByTimeStampDesc(pageable)
                .map(wordMapper::toBusinessObject)
                : this.searchWord(search, pageable);
    }

    /**
     * Perform a word search based on the japanese writing value
     *
     * @param search   Word's japanese writing value
     * @param pageable Spring pageable request properties
     * @return Spring page of retrieved corresponding words
     */
    private Page<Word> searchWord(String search, Pageable pageable) {

        var spec = WordSpecification.searchWord(search, this.converter);
        return wordRepository.findAll(spec, pageable).map(wordMapper::toBusinessObject);
    }

}
