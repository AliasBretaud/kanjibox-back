package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Word;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.web.dto.WordRequest;
import flo.no.kanji.web.dto.WordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Word mapper: bidirectional conversion between domain model, JPA entity, and API DTOs.
 *
 * @author Florian
 */
@Component
@RequiredArgsConstructor
public class WordMapper {

    private final KanjiMapper kanjiMapper;

    public Word toDomain(WordRequest request) {
        if (request == null) {
            return null;
        }
        var kanjis = request.getKanjis() != null
                ? request.getKanjis().stream().map(kanjiMapper::toDomain).toList()
                : null;
        return Word.builder()
                .value(request.getValue())
                .translations(request.getTranslations())
                .furiganaValue(request.getFuriganaValue())
                .kanjis(kanjis)
                .build();
    }

    public WordResponse toResponse(Word word) {
        if (word == null) {
            return null;
        }
        var kanjis = word.getKanjis() != null
                ? word.getKanjis().stream().map(kanjiMapper::toResponse).toList()
                : null;
        return WordResponse.builder()
                .id(word.getId())
                .value(word.getValue())
                .translations(word.getTranslations())
                .furiganaValue(word.getFuriganaValue())
                .kanjis(kanjis)
                .build();
    }

    public Word toBusinessObject(WordEntity wordEntity) {
        return toBusinessObject(wordEntity, null);
    }

    public Word toBusinessObject(WordEntity wordEntity, Integer listLimit) {
        if (wordEntity == null) {
            return null;
        }
        var kanjis = wordEntity.getKanjis() != null ? wordEntity.getKanjis().stream()
                .map(kanjiMapper::toBusinessObject)
                .collect(Collectors.toList()) : null;
        var translations = wordEntity.getTranslations() != null ? wordEntity.getTranslations().stream()
                .collect(Collectors.groupingBy(TranslationEntity::getLanguage,
                        Collectors.mapping(TranslationEntity::getTranslation, Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> listLimit != null ? list.stream().limit(listLimit).toList() : list
                        ))
                )) : null;
        return Word.builder()
                .id(wordEntity.getId())
                .value(wordEntity.getValue())
                .furiganaValue(wordEntity.getFuriganaValue())
                .translations(translations)
                .kanjis(kanjis)
                .build();
    }

    public WordEntity toEntity(Word word) {
        if (word == null) {
            return null;
        }
        var translations = word.getTranslations() != null ? word.getTranslations().entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(value -> new TranslationEntity(value, entry.getKey())))
                .toList() : null;
        return WordEntity.builder()
                .id(word.getId())
                .value(word.getValue())
                .furiganaValue(word.getFuriganaValue())
                .translations(translations)
                .build();
    }
}
