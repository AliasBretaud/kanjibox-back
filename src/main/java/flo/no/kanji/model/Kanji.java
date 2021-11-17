package flo.no.kanji.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Kanji {

	private Long id;

	private String value;

	private List<String> translations;

	private List<String> kunYomi;

	private List<String> onYomi;
	
	public Kanji(final String value) {
		this.value = value;
	}
}
