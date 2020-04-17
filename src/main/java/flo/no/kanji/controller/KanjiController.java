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

	private static final int RECENT_KANJIS_LIMIT = 10;

	@Autowired
	private KanjiService kanjiService;

	@GetMapping("/kanjis/recents")
	public List<Kanji> getRecentKanjis() {

		return kanjiService.getRecentsKanjis(RECENT_KANJIS_LIMIT);
	}

	@PostMapping("/kanjis")
	public Kanji addKanji(@RequestBody Kanji kanji,
			@RequestParam(defaultValue = "true", value = "autoDetectReadings") boolean autoDetect) {
		return kanjiService.addKanji(kanji, autoDetect);
	}
}
