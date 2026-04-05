package flo.no.kanji.conf;

import com.deepl.api.DeepLClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeeplTranslatorConfig {

    @Value("${deepl.api.key:}")
    private String deeplApiKey;

    @Bean
    public DeepLClient deeplClient() {
        return StringUtils.isBlank(deeplApiKey) ? null : new DeepLClient(deeplApiKey);
    }
}
