package flo.no.kanji.web.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
/**
 * Object returned by external Kanji readings/meanings API
 * @author Florian
 */
public class KanjiVO {
	
	/** Kanji translations **/
	private List<String> meanings;
	
	/** Chinese style readings **/
	@JsonProperty("on_readings")
	private List<String> onReadings;
	
	/** Japanese style readings **/
	@JsonProperty("kun_readings")
	private List<String> kunReadings;
}
