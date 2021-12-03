package flo.no.kanji.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KanjiVO {
	
	private List<String> meanings;
	
	@JsonProperty("on_readings")
	private List<String> onReadings;
	
	@JsonProperty("kun_readings")
	private List<String> kunReadings;
}
