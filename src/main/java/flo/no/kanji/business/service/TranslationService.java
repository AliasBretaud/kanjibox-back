package flo.no.kanji.business.service;

import flo.no.kanji.business.constants.Language;

/**
 * Translations service
 * @author Florian
 */
public interface TranslationService {

    /**
     * Translate a string value
     * @param value
     *          The value to translate
     * @param target
     *          Target language
     * @return The translated value
     */
    String translateValue(final String value, Language target);
}
