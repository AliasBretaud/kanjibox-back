package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Word;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.WordEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Word object bidirectional mapper between Model objects and Entities
 *
 * @author Florian
 */
@Service
public class WordMapper {

    @Autowired
    private KanjiMapper kanjiMapper;

    /**
     * Transforms a Word entity to business object
     *
     * @param wordEntity Input entity
     * @return Transformed business word object
     */
    public Word toBusinessObject(WordEntity wordEntity) {
        if (wordEntity == null) {
            return null;
        }
        var kanjis = wordEntity.getKanjis() != null ? wordEntity.getKanjis().stream().map(kanjiMapper::toBusinessObject)
                .collect(Collectors.toList()) : null;
        var translations = wordEntity.getTranslations() != null ? wordEntity.getTranslations().stream()
                .collect(Collectors.groupingBy(TranslationEntity::getLanguage,
                        Collectors.mapping(TranslationEntity::getTranslation, Collectors.toList())
                )) : null;
        return Word.builder()
                .id(wordEntity.getId())
                .value(wordEntity.getValue())
                .furiganaValue(wordEntity.getFuriganaValue())
                .translations(translations)
                .kanjis(kanjis)
                .build();
    }

    /**
     * Transforms a Word business object to entity (before performing save in database)
     *
     * @param word Word business object
     * @return Word entity converted object
     */
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
