package flo.no.kanji.business.service.impl;

import com.deepl.api.LanguageCode;
import com.deepl.api.Translator;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Translation service implementation based on DeepL
 * @see TranslationService
 * @author Florian
 */
@Service
@Slf4j
public class DeeplTranslationServiceImpl implements TranslationService {

    @Autowired
    private Translator translator;

    @Override
    public String translateValue(String value, Language target) {
        var lang = target == Language.EN ? LanguageCode.EnglishAmerican : target.getValue();
        try {
            var translation = translator.translateText(value, LanguageCode.Japanese, lang);
            return translation.getText();
        } catch (Exception ex) {
            log.error("Error occurred while retrieving information from deepL", ex);
        }

        return null;
    }
}
