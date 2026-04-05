package flo.no.kanji.business.service.impl;

import com.deepl.api.DeepLClient;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.service.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.web.util.HtmlUtils;
import java.util.Optional;

/**
 * Translation service implementation based on DeepL
 *
 * @author Florian
 * @see TranslationService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DeeplTranslationServiceImpl implements TranslationService {

    private final DeepLClient deeplClient;

    @Override
    public Optional<String> translateValue(String value, Language target) {
        try {
            var targetLang = target.getValue().toLowerCase();
            if ("en".equals(targetLang)) {
                targetLang = "en-US";
            }
            var translation = deeplClient.translateText(value, "ja", targetLang);
            return Optional.ofNullable(translation.getText())
                    .map(HtmlUtils::htmlUnescape);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving information from DeepL", ex);
            return Optional.empty();
        }
    }
}
