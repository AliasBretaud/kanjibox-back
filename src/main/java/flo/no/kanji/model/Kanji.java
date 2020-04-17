package flo.no.kanji.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Kanji {

	private Long id;

	private String value;

	private List<String> translations;

	private List<String> kunYomi;

	private List<String> onYomi;
}
