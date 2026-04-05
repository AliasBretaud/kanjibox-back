package flo.no.kanji.conf;

import com.deepl.api.DeepLClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeeplTranslatorConfig {

    @Bean
    @ConditionalOnProperty(name = "kanji.translation.auto.enabled", havingValue = "true")
    public DeepLClient deeplClient(@Value("${deepl.api.key}") final String deeplApiKey) {
        return new DeepLClient(deeplApiKey);
    }
}
