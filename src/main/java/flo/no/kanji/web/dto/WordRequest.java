package flo.no.kanji.web.dto;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.validator.JapaneseCharacterFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordRequest {

    @NotBlank
    @JapaneseCharacterFormat(format = {
            CharacterType.KANJI, CharacterType.KANJI_WITH_OKURIGANA,
            CharacterType.HIRAGANA, CharacterType.KATAKANA
    })
    @Size(min = 1, max = 5)
    private String value;

    private Map<Language, List<@Size(max = 50) String>> translations;

    @JapaneseCharacterFormat(format = CharacterType.HIRAGANA)
    @Size(max = 20)
    private String furiganaValue;

    private List<@Valid KanjiRequest> kanjis;
}
