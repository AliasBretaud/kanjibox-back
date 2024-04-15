package flo.no.kanji.util;

import flo.no.kanji.business.constants.Language;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TranslationUtils {

    /**
     * Null-safe method to extract a translation from a map
     *
     * @param translations Translations map
     * @param lang         Language
     * @return Found translation or null
     */
    public static List<String> getExistingTranslation(Map<Language, List<String>> translations, Language lang) {
        return Optional.ofNullable(translations)
                .map(tr -> tr.get(lang))
                .orElse(null);
    }
}
