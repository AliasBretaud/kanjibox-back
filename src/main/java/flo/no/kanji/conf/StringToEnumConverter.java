package flo.no.kanji.conf;

import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class StringToEnumConverter implements Converter<String, Language> {
    @Override
    public Language convert(@NonNull String source) {
        var lang = Language.forValue(source);
        if (lang == null) {
            throw new InvalidInputException("unknown language");
        }
        return lang;
    }
}
