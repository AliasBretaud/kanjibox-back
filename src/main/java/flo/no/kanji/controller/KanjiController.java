package flo.no.kanji.controller;

import org.apache.johnzon.core.JsonMergePatchImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import flo.no.kanji.model.Kanji;
import flo.no.kanji.service.KanjiService;

@RestController
@RequestMapping("/kanjis")
public class KanjiController {

	@Autowired
	private KanjiService kanjiService;

	@GetMapping
	public Page<Kanji> getKanjis(@RequestParam(required = false, value = "search") final String search,
			Pageable pageable) {

		return kanjiService.getKanjis(search, pageable);
	}

	@PostMapping
	public Kanji addKanji(@RequestBody Kanji kanji,
			@RequestParam(defaultValue = "true", value = "autoDetectReadings") boolean autoDetect) {
		return kanjiService.addKanji(kanji, autoDetect);
	}
	
	@PatchMapping(path = "/{kanjiId}", consumes = "application/merge-patch+json")
	public Kanji updateKanji(@PathVariable Long kanjiId,
			@RequestBody JsonMergePatchImpl patchRequest) {
		return kanjiService.patchKanji(kanjiId, patchRequest);
	}
}
