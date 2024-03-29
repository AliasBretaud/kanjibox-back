package flo.no.kanji.integration.entity;

import flo.no.kanji.business.constants.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TranslationEntity {

    @Column(name = "translation_label")
    private String translation;

    @Column(name = "translation_lang")
    @Enumerated(EnumType.STRING)
    private Language language;
}
