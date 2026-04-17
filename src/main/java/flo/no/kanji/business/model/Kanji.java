package flo.no.kanji.business.model;

import flo.no.kanji.business.constants.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Kanji domain model
 *
 * @author Florian
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kanji {

    private Long id;
    private String value;
    private Map<Language, List<String>> translations;
    private List<String> kunYomi;
    private List<String> onYomi;
    private List<String> usages;

    public Kanji(final String value) {
        this.value = value;
    }
}
