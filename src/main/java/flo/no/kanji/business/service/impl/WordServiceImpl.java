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
import flo.no.kanji.integration.entity.UserEntity;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.integration.specification.WordSpecification;
import flo.no.kanji.util.CharacterUtils;
import flo.no.kanji.util.ListUtils;
import flo.no.kanji.util.PatchHelper;
import flo.no.kanji.util.SearchQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
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
@RequiredArgsConstructor
public class WordServiceImpl implements WordService {

    private final UserService userService;
    private final KanjiService kanjiService;
    private final WordRepository wordRepository;
    // KanjiRepository is used only to fetch JPA-managed entities required for cascade persistence.
    // All business operations on kanjis go through KanjiService.
    private final KanjiRepository kanjiRepository;
    private final WordMapper wordMapper;
    private final KanjiMapper kanjiMapper;
    private final MojiDetector mojiDetector;
    private final MojiConverter mojiConverter;
    private final TranslationService translationService;
    private final PatchHelper patchHelper;

    private Executor translationExecutor;

    @Autowired
    @Qualifier("translationExecutor")
    public void setTranslationExecutor(Executor translationExecutor) {
        this.translationExecutor = translationExecutor;
    }

    @Value("${kanji.translation.auto.enable}")
    private Boolean enableAutoDefaultTranslation;

    @Value("${kanji.translation.timeout-seconds}")
    private int translationTimeoutSeconds;

    @Override
    @Transactional
    public Word addWord(Word word, boolean preview, String userSub) {
        checkWordAlreadyPresent(word, userSub);

        if (CollectionUtils.isEmpty(word.getKanjis())) {
            word.setKanjis(buildWordKanjisList(word.getValue()));
        }

        if (word.getFuriganaValue() == null && mojiDetector.hasKanji(word.getValue())) {
            word.setFuriganaValue(CharacterUtils.getWordFurigana(word.getValue()));
        }

        var user = userService.createOrGetBySub(userSub);
        var wordKanjiEntities = getExistingKanjisFromWord(word, userSub);
        var kanjisToFetch = getKanjisToFetch(word.getKanjis(), wordKanjiEntities);

        var wordTranslationFutures = buildTranslationFutures(word);
        var kanjiFutures = buildKanjiFutures(kanjisToFetch);

        wordKanjiEntities.addAll(kanjiFutures.stream()
                .map(CompletableFuture::join)
                .map(kanjiMapper::toEntity)
                .peek(k -> k.setUser(user))
                .toList());

        var translationsMap = wordTranslationFutures.entrySet().stream()
                .filter(e -> e.getValue().join() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().join()));
        word.setTranslations(translationsMap);

        if (preview) {
            word.setKanjis(wordKanjiEntities.stream().map(kanjiMapper::toBusinessObject).toList());
            return word;
        }
        return saveWord(word, wordKanjiEntities, user);
    }

    @Override
    @Transactional
    public void deleteWord(Long wordId, String userSub) {
        var entity = wordRepository.findByIdAndUserSub(wordId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Word with ID " + wordId + " not found"));
        wordRepository.delete(entity);
    }

    @Override
    @Transactional
    public Word patchWord(Long wordId, JsonNode patch, String userSub) {
        var initialEntity = wordRepository.findByIdAndUserSub(wordId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Word with ID " + wordId + " not found"));
        var patchedWord = patchHelper.mergePatch(wordMapper.toBusinessObject(initialEntity), patch, Word.class);

        if (!Objects.equals(patchedWord.getId(), wordId)) {
            throw new InvalidInputException("ID update is forbidden");
        }

        var user = userService.createOrGetBySub(userSub);
        return saveWord(patchedWord, initialEntity.getKanjis(), user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Word> getWords(String search, Language language, Integer listLimit, Pageable pageable, String userSub) {
        Page<Word> result;
        if (ObjectUtils.isEmpty(search)) {
            result = wordRepository.findAllByUserSubOrderByTimeStampDesc(userSub, pageable)
                    .map(wordMapper::toBusinessObject);
        } else {
            result = searchWord(SearchQuery.from(search, mojiConverter), language, listLimit, pageable, userSub);
        }

        if (listLimit != null && listLimit > 0) {
            result.forEach(w -> {
                if (w.getTranslations() != null) {
                    w.setTranslations(w.getTranslations().entrySet().stream()
                            .collect(Collectors.toMap(Map.Entry::getKey,
                                    e -> ListUtils.truncateList(e.getValue(), listLimit))));
                }
            });
        }
        return result;
    }

    private Word saveWord(Word word, List<KanjiEntity> wordKanjis, UserEntity user) {
        var entity = wordMapper.toEntity(word);
        if (!CollectionUtils.isEmpty(wordKanjis)) {
            wordKanjis.forEach(k -> k.setUser(user));
        }
        entity.setKanjis(wordKanjis);
        entity.setUser(user);
        return wordMapper.toBusinessObject(wordRepository.save(entity));
    }

    private List<KanjiEntity> getExistingKanjisFromWord(Word word, String userSub) {
        var kanjiValues = word.getKanjis().stream().map(Kanji::getValue).toList();
        return Collections.synchronizedList(
                new ArrayList<>(kanjiRepository.findByValueInAndUserSub(kanjiValues, userSub)));
    }

    private List<Kanji> getKanjisToFetch(List<Kanji> kanjis, List<KanjiEntity> filter) {
        return kanjis.stream()
                .filter(k -> filter.stream()
                        .map(KanjiEntity::getValue)
                        .noneMatch(ke -> ke.equals(k.getValue())))
                .toList();
    }

    private List<CompletableFuture<Kanji>> buildKanjiFutures(List<Kanji> kanjis) {
        return kanjis.stream()
                .map(kanji -> CompletableFuture.supplyAsync(() -> {
                    kanjiService.autoFillKanjiReadigs(kanji);
                    kanji.setTranslations(kanjiService.buildTranslations(kanji));
                    return kanji;
                }, translationExecutor)
                .orTimeout(translationTimeoutSeconds, TimeUnit.SECONDS)
                .exceptionally(ex -> kanji))
                .toList();
    }

    private Map<Language, CompletableFuture<List<String>>> buildTranslationFutures(Word word) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang != Language.JA)
                .collect(Collectors.toMap(
                        Function.identity(),
                        lang -> {
                            var existingTranslation = getExistingTranslation(word.getTranslations(), lang);
                            return enableAutoDefaultTranslation && CollectionUtils.isEmpty(existingTranslation)
                                    ? CompletableFuture.supplyAsync(() ->
                                            translationService.translateValue(word.getValue(), lang)
                                                    .map(List::of)
                                                    .orElse(Collections.emptyList()), translationExecutor)
                                    .orTimeout(translationTimeoutSeconds, TimeUnit.SECONDS)
                                    .exceptionally(ex -> Collections.emptyList())
                                    : CompletableFuture.completedFuture(existingTranslation);
                        }));
    }

    private void checkWordAlreadyPresent(Word word, String userSub) {
        wordRepository.findByValueAndUserSub(word.getValue(), userSub)
                .ifPresent(w -> {
                    throw new InvalidInputException(
                            String.format("Word with value '%s' already exists in database", w.getValue()));
                });
    }

    private List<Kanji> buildWordKanjisList(String wordValue) {
        return wordValue.chars().mapToObj(i -> String.valueOf((char) i))
                .filter(CharacterUtils::isKanji)
                .map(Kanji::new)
                .distinct()
                .collect(Collectors.toList());
    }

    private Page<Word> searchWord(SearchQuery query, Language language, Integer listLimit, Pageable pageable, String userSub) {
        var spec = WordSpecification.searchWord(query, userSub);
        return wordRepository.findAll(spec, pageable).map(wordMapper::toBusinessObject);
    }
}
