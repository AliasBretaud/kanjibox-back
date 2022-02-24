package flo.no.kanji.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for default HTTP client
 * 
 * @author Florian
 *
 */
@Configuration
public class RestTemplateConfig {

	/**
	 * Create default HTTP RestTemplate
	 * 
	 * @return
	 * 		Standard restTemplate
	 */
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
