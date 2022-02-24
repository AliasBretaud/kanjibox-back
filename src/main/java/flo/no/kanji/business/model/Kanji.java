package flo.no.kanji.business.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kanji model object representation
 * 
 * @author Florian
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class Kanji {

	/** Kanji technical identifier **/
	private Long id;

	/** Kanji japanese value **/
	private String value;

	/** Kanji translations **/
	private List<String> translations;

	/** Kanji japanese style reading styles (in hiragana) **/
	private List<String> kunYomi;

	/** Kanji chinese style reading styles (in hiragana) **/
	private List<String> onYomi;
	
	public Kanji(final String value) {
		this.value = value;
	}
}
