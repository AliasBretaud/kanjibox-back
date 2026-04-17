package flo.no.kanji.web.dto;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.validator.JapaneseCharacterFormat;
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
public class KanjiRequest {

    @NotBlank
    @Size(min = 1, max = 1)
    @JapaneseCharacterFormat(format = CharacterType.KANJI)
    private String value;

    private Map<Language, List<@Size(max = 50) String>> translations;

    @JapaneseCharacterFormat(format = CharacterType.HIRAGANA)
    private List<@Size(max = 15) String> kunYomi;

    @JapaneseCharacterFormat(format = CharacterType.KATAKANA)
    private List<@Size(max = 5) String> onYomi;
}
