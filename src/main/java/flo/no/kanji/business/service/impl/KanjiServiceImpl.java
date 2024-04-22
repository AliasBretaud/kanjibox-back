package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.specification.KanjiSpecification;
import flo.no.kanji.util.PatchHelper;
import io.github.aliasbretaud.mojibox.dictionary.KanjiDictionary;
import io.github.aliasbretaud.mojibox.enums.MeaningLanguage;
import io.github.aliasbretaud.mojibox.enums.ReadingType;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
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
public class KanjiServiceImpl implements KanjiService {

    /** Kanji JPA repository **/
    @Autowired
    private KanjiRepository kanjiRepository;

    /** Kanji business/entity object mapper */
    @Autowired
    private KanjiMapper kanjiMapper;

    /** External kanji dictionary */
    @Autowired
    private KanjiDictionary kanjiDictionary;

    /** Kanji updating fields class helper */
    @Autowired
    private PatchHelper patchHelper;

    /** Japanese alphabets converting service **/
    @Autowired
    private MojiConverter converter;

    /**
     * {@inheritDoc}
     */
    @Override
    public Kanji addKanji(@Valid Kanji kanji, boolean autoDetectReadings, boolean preview) {

        // Check duplicate entry
        checkKanjiAlreadyPresent(kanji);

        // Handle auto-detect readings
        if (autoDetectReadings) {
            autoFillKanjiReadigs(kanji);
        }

        // Get all translations
        var translations = buildTranslations(kanji);
        kanji.setTranslations(translations);

        // Built object
        return preview ? kanji : saveKanji(kanji);
    }

    private Kanji saveKanji(final Kanji kanji) {
        var entity = kanjiMapper.toEntity(kanji);
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
                    kanji.setKunYomi(kanjiVo.getReading(ReadingType.JA_KUN));
                    kanji.setOnYomi(kanjiVo.getReading(ReadingType.JA_ON));
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Kanji> getKanjis(String search, Language language, Integer listLimit, Pageable pageable) {

        return ObjectUtils.isEmpty(search)
                ? kanjiRepository.findAllByOrderByTimeStampDesc(pageable)
                .map(k -> kanjiMapper.toBusinessObject(k, listLimit))
                : this.searchKanji(search, language, listLimit, pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Kanji patchKanji(Long kanjiId, JsonNode patch) {

        var initialKanji = this.findById(kanjiId);
        var patchedKanji = patchHelper.mergePatch(initialKanji, patch, Kanji.class);

        // Prevent ID update
        if (!Objects.equals(patchedKanji.getId(), kanjiId)) {
            throw new InvalidInputException("ID update is forbidden");
        }

        var patchedKanjiEntity = kanjiMapper.toEntity(patchedKanji);
        patchedKanjiEntity.setTimeStamp(LocalDateTime.now());
        patchedKanjiEntity = kanjiRepository.save(patchedKanjiEntity);

        return kanjiMapper.toBusinessObject(patchedKanjiEntity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Kanji findById(Long kanjiId) {
        return kanjiMapper.toBusinessObject(kanjiRepository.findById(kanjiId)
                .orElseThrow(() -> new ItemNotFoundException("Kanji with ID " + kanjiId + " not found")));
    }

    /**
     * Execute a search query on different readings, translations or kanji value
     *
     * @param search   The input search value
     * @param pageable Pagination parameter
     * @return The result of search
     */
    private Page<Kanji> searchKanji(String search, Language language, Integer listLimit, Pageable pageable) {

        var spec = KanjiSpecification.searchKanji(search, language, this.converter);
        // Execute query, mapping and return results
        return kanjiRepository.findAll(spec, pageable).map(k -> kanjiMapper.toBusinessObject(k, listLimit));
    }

    private List<String> findDictionaryTranslations(String kanjiValue, Language language) {
        var entry = kanjiDictionary.searchKanji(kanjiValue);
        return Optional.ofNullable(entry)
                .map(e -> Arrays.stream(MeaningLanguage.values())
                        .filter(ml -> ml.name().equalsIgnoreCase(language.getValue()))
                        .findFirst()
                        .map(e::getMeaning)
                        .orElse(Collections.emptyList()))
                .orElse(Collections.emptyList());
    }

    private void checkKanjiAlreadyPresent(final Kanji kanji) {
        Optional.ofNullable(kanjiRepository.findByValue(kanji.getValue())).ifPresent(k -> {
            throw new InvalidInputException(
                    String.format("Kanji with value '%s' already exists in database", k.getValue()));
        });
    }

}
