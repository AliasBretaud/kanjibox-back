package flo.no.kanji.business.service.impl;

import com.google.cloud.translate.Translate;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.web.util.HtmlUtils;
import java.util.Optional;

/**
 * Translation service implementation based on Google
 *
 * @author Florian
 * @see TranslationService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleTranslationServiceImpl implements TranslationService {

    private final Translate googleTranslate;

    @Override
    public Optional<String> translateValue(String value, Language target) {
        try {
            var translation = googleTranslate.translate(value, Translate.TranslateOption.sourceLanguage("ja"),
                    Translate.TranslateOption.targetLanguage(target.getValue()));
            return Optional.ofNullable(translation.getTranslatedText())
                    .map(HtmlUtils::htmlUnescape);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving information from Google", ex);
            return Optional.empty();
        }
    }
}
