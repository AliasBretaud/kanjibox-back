package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Translation;
import flo.no.kanji.integration.entity.TranslationEntity;
import org.springframework.stereotype.Service;

@Service
public class TranslationMapper {

    public Translation toBusinessObject(TranslationEntity entity) {
        return Translation.builder()
                .text(entity.getTranslation())
                .language(entity.getLanguage())
                .build();
    }

    public TranslationEntity toEntity(Translation translation) {
        return TranslationEntity.builder()
                .translation(translation.getText())
                .language(translation.getLanguage())
                .build();
    }
}
