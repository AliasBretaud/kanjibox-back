package flo.no.kanji.business.model;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.business.validator.JapaneseCharacterFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kanji model object representation
 * 
 * @author Florian
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kanji {

	/** Kanji technical identifier **/
	private Long id;

	/** Kanji japanese value **/
	@NotBlank
	@Size(min = 1, max = 1)
	@JapaneseCharacterFormat(format = CharacterType.KANJI)
	private String value;

	/** Kanji translations **/
	private List<String> translations;

	/** Kanji japanese style reading styles (in hiragana) **/
	@JapaneseCharacterFormat(format = CharacterType.HIRAGANA)
	private List<String> kunYomi;

	/** Kanji chinese style reading styles (in hiragana) **/
	@JapaneseCharacterFormat(format = CharacterType.KATAKANA)
	private List<String> onYomi;
	
	public Kanji(final String value) {
		this.value = value;
	}
}
