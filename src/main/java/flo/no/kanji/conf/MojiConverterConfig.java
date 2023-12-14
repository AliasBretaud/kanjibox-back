package flo.no.kanji.conf;

import com.moji4j.MojiConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
