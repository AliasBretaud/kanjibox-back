package flo.no.kanji.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import flo.no.kanji.api.model.KanjiVO;

/**
 * Kanji readings and translations external API REST HTTP Client
 * 
 * @author Florian
 *
 */
@Service
public class KanjiApiClient {

	/** resource URL **/
	private static final String URL = "https://kanjiapi.dev/v1/kanji/%s";

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
		return restTemplate.getForObject(String.format(URL, kanjiValue), KanjiVO.class);
	}

}
