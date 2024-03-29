package flo.no.kanji.business.model;

import flo.no.kanji.business.constants.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Translation {

    private String text;

    private Language language;
}
