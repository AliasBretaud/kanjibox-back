package flo.no.kanji.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import flo.no.kanji.business.constants.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class WordResponse {

    private Long id;
    private String value;
    private Map<Language, List<String>> translations;
    private String furiganaValue;
    private List<KanjiResponse> kanjis;
}
