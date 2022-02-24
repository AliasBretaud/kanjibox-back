package flo.no.kanji.business.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Word (Kanji 1+ and optional okuriganas)  model object representation
 * 
 * @author Florian
 *
 */
@Getter
@Setter
public class Word {

	/** Word technical identifier */
	private Long id;

	/** Word japanese value */
	private String value;

	/** Word translations */
	private String translation;

	/** Word transcription in hiragana **/
	private String furiganaValue;

	/** Kanjis composing the word */
	private List<Kanji> kanjis;
}
