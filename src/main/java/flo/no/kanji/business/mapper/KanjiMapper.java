package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
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
        if (kanjiEntity == null) {
            return null;
        }
        var translations = kanjiEntity.getTranslations() != null ?
                kanjiEntity.getTranslations().stream()
                        .collect(Collectors.groupingBy(TranslationEntity::getLanguage,
                                Collectors.mapping(TranslationEntity::getTranslation, Collectors.toList())
                        )) : null;
        return Kanji.builder()
                .id(kanjiEntity.getId())
                .kunYomi(kanjiEntity.getKunYomi())
                .onYomi(kanjiEntity.getOnYomi())
                .translations(translations)
                .value(kanjiEntity.getValue())
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
