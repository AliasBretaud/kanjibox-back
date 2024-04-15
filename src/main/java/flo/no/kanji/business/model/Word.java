package flo.no.kanji.business.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.validator.JapaneseCharacterFormat;
import jakarta.validation.constraints.NotBlank;
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
    @JapaneseCharacterFormat(format = {CharacterType.KANJI, CharacterType.KANJI_WITH_OKURIGANA})
    private String value;

    /** Word translations */
    private Map<Language, List<String>> translations;

    /** Word transcription in hiragana **/
    @NotBlank
    @JapaneseCharacterFormat(format = CharacterType.HIRAGANA)
    private String furiganaValue;

    /** Kanjis composing the word */
    private List<Kanji> kanjis;
}
