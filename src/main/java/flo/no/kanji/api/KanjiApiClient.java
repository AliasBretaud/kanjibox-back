package flo.no.kanji.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import flo.no.kanji.api.model.KanjiVO;

@Service
public class KanjiApiClient {

	private static final String URL = "https://kanjiapi.dev/v1/kanji/%s";

	@Autowired
	private RestTemplate restTemplate;

	public KanjiVO searchKanjiReadings(String kanjiValue) {
		return restTemplate.getForObject(String.format(URL, kanjiValue), KanjiVO.class);
	}

}
