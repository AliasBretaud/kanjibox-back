package flo.no.kanji.business.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.aliasbretaud.mojibox.KanjiDictionary;
import com.github.aliasbretaud.mojibox.ReadingType;
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
    public Kanji addKanji(@Valid Kanji kanji, boolean autoDetectReadings) {

        // Check duplicate entry
        checkKanjiAlreadyPresent(kanji);

        // Handle auto-detect readings
        if (autoDetectReadings) {
            autoFillKanjiReadigs(kanji);
        }

        // Check if the kanji contains at least one default translation
        if (!hasDefaultTranslation(kanji)) {
            handleDefaultTranslation(kanji);
        }

        return kanjiMapper.toBusinessObject(kanjiRepository.save(kanjiMapper.toEntity(kanji)));
    }

    private void handleDefaultTranslation(Kanji kanji) {
        var translations = new HashMap<>(kanji.getTranslations());
        var kanjiVo = kanjiDictionary.searchKanji(kanji.getValue());
        var defaultTranslation = kanjiVo.getMeanings()
                .get(com.github.aliasbretaud.mojibox.Language.EN);
        if (defaultTranslation != null && !defaultTranslation.isEmpty()) {
            translations.put(Language.EN, defaultTranslation);
        }
        kanji.setTranslations(translations);
    }

    private boolean hasDefaultTranslation(Kanji kanji) {
        var translations = kanji.getTranslations();
        return translations != null && translations.containsKey(Language.EN);
    }

    private void checkKanjiAlreadyPresent(final Kanji kanji) {
        Optional.ofNullable(kanjiRepository.findByValue(kanji.getValue())).ifPresent(k -> {
            throw new InvalidInputException(
                    String.format("Kanji with value '%s' already exists in database", k.getValue()));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void autoFillKanjiReadigs(Kanji kanji) {
        var kanjiVo = kanjiDictionary.searchKanji(kanji.getValue());
        if (kanjiVo != null) {
            kanji.setKunYomi(kanjiVo.getReadings().get(ReadingType.JA_KUN));
            kanji.setOnYomi(kanjiVo.getReadings().get(ReadingType.JA_ON));
            kanji.setTranslations(buildTranslationsFromVo(kanjiVo));
        }

    }

    private Map<Language, List<String>> buildTranslationsFromVo(com.github.aliasbretaud.mojibox.Kanji kanjiVo) {
        var translations = new HashMap<>(Map.of(
                Language.EN, kanjiVo.getMeanings().get(com.github.aliasbretaud.mojibox.Language.EN)
                        .stream().limit(3).toList()));
        var frTranslations = kanjiVo.getMeanings().get(com.github.aliasbretaud.mojibox.Language.FR);
        if (frTranslations != null && !frTranslations.isEmpty()) {
            translations.put(Language.FR, frTranslations.stream().limit(3).toList());
        }
        return translations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<Kanji> getKanjis(String search, Language language, Pageable pageable) {

        return ObjectUtils.isEmpty(search)
                ? kanjiRepository.findAllByOrderByTimeStampDesc(pageable)
                .map(kanjiMapper::toBusinessObject)
                : this.searchKanji(search, language, pageable);
    }

    /**
     * Execute a search query on different readings, translations or kanji value
     *
     * @param search   The input search value
     * @param pageable Pagination parameter
     * @return The result of search
     */
    private Page<Kanji> searchKanji(String search, Language language, Pageable pageable) {

        var spec = KanjiSpecification.searchKanji(search, language, this.converter);
        // Execute query, mapping and return results
        return kanjiRepository.findAll(spec, pageable).map(kanjiMapper::toBusinessObject);
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

}
