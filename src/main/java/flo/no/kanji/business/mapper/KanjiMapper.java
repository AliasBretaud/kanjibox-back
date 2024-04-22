package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.util.ListUtils;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Kanji object bidirectional mapper between Model objects and Entities
 *
 * @author Florian
 */
@Service
public class KanjiMapper {

    /**
     * Transforms a Kanji entity to business object
     *
     * @param kanjiEntity Input entity
     * @return Transformed business kanji object
     */
    public Kanji toBusinessObject(KanjiEntity kanjiEntity) {
        return toBusinessObject(kanjiEntity, null);
    }

    /**
     * Transforms a Kanji entity to business object
     *
     * @param kanjiEntity Input entity
     * @param listLimit   Max size of lists contained in the object to convert
     * @return Transformed business kanji object
     */
    public Kanji toBusinessObject(KanjiEntity kanjiEntity, Integer listLimit) {
        if (kanjiEntity == null) {
            return null;
        }
        var onYomi = kanjiEntity.getOnYomi();
        var kunYomi = kanjiEntity.getKunYomi();
        var translations = kanjiEntity.getTranslations() != null ?
                kanjiEntity.getTranslations().stream()
                        .collect(Collectors.groupingBy(TranslationEntity::getLanguage,
                                Collectors.mapping(TranslationEntity::getTranslation,
                                        Collectors.collectingAndThen(
                                                Collectors.toList(),
                                                list -> listLimit != null ? list.stream()
                                                        .limit(listLimit)
                                                        .toList() : list
                                        )
                                )
                        )) : null;

        return Kanji.builder()
                .id(kanjiEntity.getId())
                .value(kanjiEntity.getValue())
                .onYomi(listLimit != null ? ListUtils.truncateList(onYomi, listLimit) : onYomi)
                .kunYomi(listLimit != null ? ListUtils.truncateList(kunYomi, listLimit) : kunYomi)
                .translations(translations)
                .build();
    }

    /**
     * Transforms a Kanji business object to entity (before performing save in database)
     *
     * @param kanji Kanji business object
     * @return Kanji entity converted object
     */
    public KanjiEntity toEntity(Kanji kanji) {
        if (kanji == null) {
            return null;
        }
        var translations = kanji.getTranslations() != null ? kanji.getTranslations()
                .entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> new TranslationEntity(value, entry.getKey())))
                .toList() : null;
        return KanjiEntity.builder()
                .id(kanji.getId())
                .kunYomi(kanji.getKunYomi())
                .onYomi(kanji.getOnYomi())
                .translations(translations)
                .value(kanji.getValue())
                .build();
    }
}
