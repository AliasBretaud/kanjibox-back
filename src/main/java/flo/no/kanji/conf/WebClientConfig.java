package flo.no.kanji.conf;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * Configuration class for default HTTP client
 *
 * @author Florian
 */
@Configuration
public class WebClientConfig {

    /**
     * Create default HTTP WebClient
     *
     * @return Standard webClient
     */
    @Bean
    public WebClient getWebClient() {
        HttpClient client = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(client)).build();
    }
}
