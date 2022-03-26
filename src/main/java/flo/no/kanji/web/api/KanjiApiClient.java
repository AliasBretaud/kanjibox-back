package flo.no.kanji.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import flo.no.kanji.web.api.model.KanjiVO;

/**
 * Kanji readings and translations external API REST HTTP Client
 * @author Florian
 */
@Service
@PropertySource("classpath:kanjiExternalApi.properties")
public class KanjiApiClient {

	/** resource URL **/
	@Value("${kanji.external.api.url}")
	private String URL;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Retrieve kanji translations and readings
	 * 
	 * @param kanjiValue
	 * 			Kanji in japanese character
	 * @return
	 * 		KanjiVO containing translations and readings
	 */
	public KanjiVO searchKanjiReadings(String kanjiValue) {
		return restTemplate.getForObject(URL + kanjiValue, KanjiVO.class);
	}

}
