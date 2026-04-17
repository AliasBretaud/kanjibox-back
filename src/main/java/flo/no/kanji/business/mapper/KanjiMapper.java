package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.web.dto.KanjiRequest;
import flo.no.kanji.web.dto.KanjiResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Kanji mapper: bidirectional conversion between domain model, JPA entity, and API DTOs.
 *
 * @author Florian
 */
@Component
public class KanjiMapper {

    public Kanji toDomain(KanjiRequest request) {
        if (request == null) {
            return null;
        }
        return Kanji.builder()
                .value(request.getValue())
                .translations(request.getTranslations())
                .kunYomi(request.getKunYomi())
                .onYomi(request.getOnYomi())
                .build();
    }

    public KanjiResponse toResponse(Kanji kanji) {
        if (kanji == null) {
            return null;
        }
        return KanjiResponse.builder()
                .id(kanji.getId())
                .value(kanji.getValue())
                .translations(kanji.getTranslations())
                .kunYomi(kanji.getKunYomi())
                .onYomi(kanji.getOnYomi())
                .usages(kanji.getUsages())
                .build();
    }

    public Kanji toBusinessObject(KanjiEntity kanjiEntity) {
        if (kanjiEntity == null) {
            return null;
        }
        var translations = kanjiEntity.getTranslations() != null ?
                kanjiEntity.getTranslations().stream()
                        .collect(Collectors.groupingBy(TranslationEntity::getLanguage,
                                Collectors.mapping(TranslationEntity::getTranslation,
                                        Collectors.toList()
                                )
                        )) : null;
        var usages = kanjiEntity.getWords() != null ? kanjiEntity.getWords()
                .stream().map(WordEntity::getValue).toList() : null;

        return Kanji.builder()
                .id(kanjiEntity.getId())
                .value(kanjiEntity.getValue())
                .onYomi(kanjiEntity.getOnYomi())
                .kunYomi(kanjiEntity.getKunYomi())
                .translations(translations)
                .usages(usages)
                .build();
    }

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
