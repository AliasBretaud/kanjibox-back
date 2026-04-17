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
import flo.no.kanji.util.SearchQuery;
import io.github.aliasbretaud.mojibox.dictionary.KanjiDictionary;
import io.github.aliasbretaud.mojibox.enums.MeaningLanguage;
import io.github.aliasbretaud.mojibox.enums.ReadingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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
@RequiredArgsConstructor
public class KanjiServiceImpl implements KanjiService {

    private static final int LISTS_MAX_SIZE = 3;

    private final UserService userService;
    private final KanjiRepository kanjiRepository;
    private final KanjiMapper kanjiMapper;
    private final KanjiDictionary kanjiDictionary;
    private final PatchHelper patchHelper;
    private final MojiConverter converter;

    @Override
    @Transactional
    public Kanji addKanji(Kanji kanji, boolean autoDetect, boolean preview, String userSub) {
        checkKanjiAlreadyPresent(kanji, userSub);
        if (autoDetect) {
            autoFillKanjiReadigs(kanji);
        }
        kanji.setTranslations(buildTranslations(kanji));
        return preview ? kanji : saveKanji(kanji, userSub);
    }

    @Override
    public Map<Language, List<String>> buildTranslations(Kanji kanji) {
        return Arrays.stream(Language.values())
                .filter(lang -> lang != Language.JA)
                .map(lang -> Map.entry(lang, Optional.ofNullable(getExistingTranslation(kanji.getTranslations(), lang))
                        .orElseGet(() -> findDictionaryTranslations(kanji.getValue(), lang))))
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void autoFillKanjiReadigs(Kanji kanji) {
        Optional.ofNullable(kanjiDictionary.searchKanji(kanji.getValue()))
                .ifPresent(kanjiVo -> {
                    kanji.setKunYomi(ListUtils.truncateList(kanjiVo.getReading(ReadingType.JA_KUN), LISTS_MAX_SIZE));
                    kanji.setOnYomi(ListUtils.truncateList(kanjiVo.getReading(ReadingType.JA_ON), LISTS_MAX_SIZE));
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Kanji> getKanjis(String search, Language language, Pageable pageable, String userSub) {
        return ObjectUtils.isEmpty(search)
                ? kanjiRepository.findAllByUserSubOrderByTimeStampDesc(userSub, pageable)
                        .map(kanjiMapper::toBusinessObject)
                : searchKanji(SearchQuery.from(search, converter), language, pageable, userSub);
    }

    @Override
    @Transactional
    public Kanji patchKanji(Long kanjiId, JsonNode patch, String userSub) {
        var initialKanji = findById(kanjiId, userSub);
        var patchedKanji = patchHelper.mergePatch(initialKanji, patch, Kanji.class);

        if (!Objects.equals(patchedKanji.getId(), kanjiId)) {
            throw new InvalidInputException("ID update is forbidden");
        }

        var patchedKanjiEntity = kanjiMapper.toEntity(patchedKanji);
        patchedKanjiEntity.setUser(userService.createOrGetBySub(userSub));
        return kanjiMapper.toBusinessObject(kanjiRepository.save(patchedKanjiEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public Kanji findById(Long kanjiId, String userSub) {
        return kanjiMapper.toBusinessObject(kanjiRepository.findByIdAndUserSub(kanjiId, userSub)
                .orElseThrow(() -> new ItemNotFoundException("Kanji with ID " + kanjiId + " not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Kanji> findByValues(List<String> values, String userSub) {
        return kanjiRepository.findByValueInAndUserSub(values, userSub)
                .stream().map(kanjiMapper::toBusinessObject).toList();
    }

    @Override
    @Transactional
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

    private Kanji saveKanji(Kanji kanji, String userSub) {
        var entity = kanjiMapper.toEntity(kanji);
        entity.setUser(userService.createOrGetBySub(userSub));
        return kanjiMapper.toBusinessObject(kanjiRepository.save(entity));
    }

    private Page<Kanji> searchKanji(SearchQuery query, Language language, Pageable pageable, String userSub) {
        var spec = KanjiSpecification.searchKanji(query, language, userSub);
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

    private void checkKanjiAlreadyPresent(Kanji kanji, String userSub) {
        kanjiRepository.findByValueAndUserSub(kanji.getValue(), userSub)
                .ifPresent(k -> {
                    throw new InvalidInputException(
                            String.format("Kanji with value '%s' already exists in database", k.getValue()));
                });
    }
}
