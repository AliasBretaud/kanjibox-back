package flo.no.kanji.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Word (Kanji 1+ and optional okuriganas)  model object representation
 *
 * @author Florian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Word implements Serializable {

    /** Word technical identifier */
    private Long id;

    /** Word japanese value */
    @NotBlank
    @JapaneseCharacterFormat(format = {
            CharacterType.KANJI, CharacterType.KANJI_WITH_OKURIGANA,
            CharacterType.HIRAGANA, CharacterType.KATAKANA
    })
    @Size(min = 1, max = 5)
    private String value;

    /** Word translations */
    private Map<Language, List<@Size(max = 50) String>> translations;

    /** Word transcription in hiragana **/
    @JapaneseCharacterFormat(format = CharacterType.HIRAGANA)
    @Size(max = 20)
    private String furiganaValue;

    /** Kanjis composing the word */
    private List<@Valid Kanji> kanjis;
}
