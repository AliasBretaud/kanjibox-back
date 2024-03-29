package flo.no.kanji.conf;

import com.deepl.api.Translator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:deeplApi.properties")
public class DeeplTranslatorConfig {

    @Value("${deepl.api.auth.key}")
    private String authKey;

    @Bean
    public Translator deeplTranslator() {
        return new Translator(authKey);
    };
}
