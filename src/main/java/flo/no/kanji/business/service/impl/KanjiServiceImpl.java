package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.UserService;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.specification.KanjiSpecification;
import flo.no.kanji.util.ListUtils;
import flo.no.kanji.util.PatchHelper;
import io.github.aliasbretaud.mojibox.dictionary.KanjiDictionary;
import io.github.aliasbretaud.mojibox.enums.MeaningLanguage;
import io.github.aliasbretaud.mojibox.enums.ReadingType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static flo.no.kanji.util.TranslationUtils.getExistingTranslation;

/**
 * Kanji business service implementation
 *
 * @author Florian
 * @see KanjiService
 */
@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class KanjiServiceImpl implements KanjiService {

    private static final int LISTS_MAX_SIZE = 3;

    private final UserService userService;

    /**
     * Kanji JPA repository
     **/
    private final KanjiRepository kanjiRepository;

    /**
     * Kanji business/entity object mapper
     */
    private final KanjiMapper kanjiMapper;

    /**
     * External kanji dictionary
     */
    private final KanjiDictionary kanjiDictionary;

    /**
     * Kanji updating fields class helper
     */
    private final PatchHelper patchHelper;

    /**
     * Japanese alphabets converting service
     **/
    private final MojiConverter converter;

    @Override
    public Kanji addKanji(@Valid Kanji kanji, boolean autoDetect, boolean preview, String userSub) {

        // Check duplicate entry
        checkKanjiAlreadyPresent(kanji, userSub);

        // Handle auto-detect readings
        if (autoDetect) {
            autoFillKanjiReadigs(kanji);
        }

        // Get all translations
        var translations = buildTranslations(kanji);
        kanji.setTranslations(translations);

        // Built object
        return preview ? kanji : saveKanji(kanji, userSub);
    }

    private Kanji saveKanji(final Kanji kanji, String userSub) {
        var entity = kanjiMapper.toEntity(kanji);
        var user = userService.createOrGetBySub(userSub);
        entity.setUser(user);
        return kanjiMapper.toBusinessObject(kanjiRepository.save(entity));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Language, List<String>> buildTranslations(Kanji kanji) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang != Language.JA)
                .map(lang -> Map.entry(lang, Optional.ofNullable(getExistingTranslation(kanji.getTranslations(), lang))
                        .orElseGet(() -> findDictionaryTranslations(kanji.getValue(), lang))))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void autoFillKanjiReadigs(Kanji kanji) {
        Optional.ofNullable(kanjiDictionary.searchKanji(kanji.getValue()))
                .ifPresent(kanjiVo -> {
                    kanji.setKunYomi(
                            ListUtils.truncateList(kanjiVo.getReading(ReadingType.JA_KUN), LISTS_MAX_SIZE));
                    kanji.setOnYomi(
                            ListUtils.truncateList(kanjiVo.getReading(ReadingType.JA_ON), LISTS_MAX_SIZE));
                });
    }

    @Override
    public Page<Kanji> getKanjis(String search, Language language, Pageable pageable, String userSub) {
        return ObjectUtils.isEmpty(search)
                ? kanjiRepository.findAllByUserSubOrderByTimeStampDesc(userSub, pageable)
                .map(kanjiMapper::toBusinessObject)
                : this.searchKanji(search, language, pageable, userSub);
    }

    @Override
    public Kanji patchKanji(Long kanjiId, JsonNode patch, String userSub) {

        var initialKanji = this.findById(kanjiId, userSub);
        var patchedKanji = patchHelper.mergePatch(initialKanji, patch, Kanji.class);

        // Prevent ID update
        if (!Objects.equals(patchedKanji.getId(), kanjiId)) {
            throw new InvalidInputException("ID update is forbidden");
        }

        var patchedKanjiEntity = kanjiMapper.toEntity(patchedKanji);
        patchedKanjiEntity.setUser(userService.createOrGetBySub(userSub));
        patchedKanjiEntity = kanjiRepository.save(patchedKanjiEntity);

        return kanjiMapper.toBusinessObject(patchedKanjiEntity);
    }

    @Override
    public Kanji findById(Long kanjiId, String userSub) {
        return kanjiMapper.toBusinessObject(kanjiRepository.findByIdAndUserSub(kanjiId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Kanji with ID " + kanjiId + " not found")));
    }

    @Override
    public void deleteKanji(Long kanjiId, String userSub) {
        var entity = kanjiRepository.findByIdAndUserSub(kanjiId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Kanji with ID " + kanjiId + " not found"));
        if (!entity.getWords().isEmpty()) {
            var usedInWords = entity.getWords().stream()
                    .map(WordEntity::getValue)
                    .collect(Collectors.joining(", "));
            throw new InvalidInputException("Kanji is still referenced by words: [" + usedInWords + "]");
        }
        kanjiRepository.delete(entity);
    }

    /**
     * Execute a search query on different readings, translations or kanji value
     *
     * @param search   The input search value
     * @param pageable Pagination parameter
     * @return The result of search
     */
    private Page<Kanji> searchKanji(String search, Language language, Pageable pageable, String userSub) {

        var spec = KanjiSpecification.searchKanji(search, language, this.converter, userSub);
        // Execute query, mapping and return results
        return kanjiRepository.findAll(spec, pageable).map(kanjiMapper::toBusinessObject);
    }

    private List<String> findDictionaryTranslations(String kanjiValue, Language language) {
        var entry = kanjiDictionary.searchKanji(kanjiValue);
        return Optional.ofNullable(entry)
                .map(e -> Arrays.stream(MeaningLanguage.values())
                        .filter(ml -> ml.name().equalsIgnoreCase(language.getValue()))
                        .findFirst()
                        .map(l -> ListUtils.truncateList(e.getMeaning(l), LISTS_MAX_SIZE))
                        .orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
    }

    private void checkKanjiAlreadyPresent(final Kanji kanji, String userSub) {
        kanjiRepository.findByValueAndUserSub(kanji.getValue(), userSub)
                .ifPresent(k -> {
                    throw new InvalidInputException(
                            String.format("Kanji with value '%s' already exists in database", k.getValue()));
                });
    }

}
