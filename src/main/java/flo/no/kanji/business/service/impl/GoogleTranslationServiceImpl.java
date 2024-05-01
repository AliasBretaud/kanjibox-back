package flo.no.kanji.business.service.impl;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.web.util.HtmlUtils.htmlUnescape;

/**
 * Translation service implementation based on Google
 *
 * @author Florian
 * @see TranslationService
 */
@Service
@Slf4j
public class GoogleTranslationServiceImpl implements TranslationService {

    @Autowired
    private Translate googleTranslate;

    @Override
    public String translateValue(String value, Language target) {
        try {
            var translation = googleTranslate.translate(value, TranslateOption.sourceLanguage("ja"),
                    TranslateOption.targetLanguage(target.getValue()));
            var res = translation.getTranslatedText();
            return res != null ? htmlUnescape(res) : null;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving information from Google", ex);
        }

        return null;
    }
}
