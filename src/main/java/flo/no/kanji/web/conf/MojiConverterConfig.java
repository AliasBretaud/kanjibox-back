package flo.no.kanji.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moji4j.MojiConverter;

/**
 * Injects global MojiConverter for Japanese translations util bean
 * @author Florian
 */
@Configuration
public class MojiConverterConfig {

	/**
	 * Creates default converter
	 * @return
	 */
	@Bean
	public MojiConverter getMojiConverter() {
		return new MojiConverter();
	}
}
