package flo.no.kanji.business.model;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private String value;

	/** Word translations */
	private String translation;

	/** Word transcription in hiragana **/
	private String furiganaValue;

	/** Kanjis composing the word */
	private List<Kanji> kanjis;
}
