package flo.no.kanji.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import flo.no.kanji.model.Kanji;
import flo.no.kanji.service.KanjiService;

@RestController
public class KanjiController {

	@Autowired
	private KanjiService kanjiService;

	@GetMapping("/kanjis")
	public List<Kanji> getKanjis(@RequestParam(required = false, value = "limit") Integer limit) {

		return kanjiService.getKanjis(limit);
	}

	@PostMapping("/kanjis")
	public Kanji addKanji(@RequestBody Kanji kanji,
			@RequestParam(defaultValue = "true", value = "autoDetectReadings") boolean autoDetect) {
		return kanjiService.addKanji(kanji, autoDetect);
	}
}
