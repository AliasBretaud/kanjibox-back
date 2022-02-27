package flo.no.kanji.web.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.moji4j.MojiConverter;

@Configuration
public class MojiConverterConfig {

	@Bean
	public MojiConverter getMojiConverter() {
		return new MojiConverter();
	}
}
