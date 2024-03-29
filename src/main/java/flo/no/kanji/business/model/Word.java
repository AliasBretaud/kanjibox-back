package flo.no.kanji.business.model;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.business.validator.JapaneseCharacterFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Word (Kanji 1+ and optional okuriganas)  model object representation
 * @author Florian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Word {

	/** Word technical identifier */
	private Long id;

	/** Word japanese value */
	@NotBlank
	@JapaneseCharacterFormat(format = {CharacterType.KANJI, CharacterType.KANJI_WITH_OKURIGANA})
	private String value;

	/** Word translations */
	private List<Translation> translations;

	/** Word transcription in hiragana **/
	@NotBlank
	@JapaneseCharacterFormat(format = CharacterType.HIRAGANA)
	private String furiganaValue;

	/** Kanjis composing the word */
	private List<Kanji> kanjis;
}
