package flo.no.kanji.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Word {

	private Long id;

	private String value;

	private String translation;

	private String furiganaValue;

	private List<Kanji> kanjis;
}
