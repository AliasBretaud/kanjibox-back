package flo.no.kanji.conf;

import com.deepl.api.DeepLClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeeplTranslatorConfig {

    @Value("${deepl.api.key:}")
    private String deeplApiKey;

    @Bean
    public DeepLClient deeplClient() {
        return new DeepLClient(deeplApiKey);
    }
}
