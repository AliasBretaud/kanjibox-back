package flo.no.kanji.conf;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleTranslatorConfig {

    @Bean
    public Translate googleTranslator() {
        return TranslateOptions.getDefaultInstance().getService();
    }
}
