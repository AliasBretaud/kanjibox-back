package flo.no.kanji.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KanjiVO {
	private List<String> meanings;
	private List<String> on_readings;
	private List<String> kun_readings;
}
