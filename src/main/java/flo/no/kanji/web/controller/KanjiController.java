package flo.no.kanji.web.controller;

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

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;

/**
 * Kanji REST Controller
 * 
 * @author Florian
 *
 */
@RestController
@RequestMapping("/kanjis")
public class KanjiController {

	/** Kanji business service **/
	@Autowired
	private KanjiService kanjiService;

	/**
	 * Search kanjis
	 * 
	 * @param search
	 * 			Japanese kanji value
	 * @param pageable
	 * 			Returned page parameters (limit, number of items per page...)
	 * @return
	 * 			Spring page of retrieved corresponding kanjis
	 */
	@GetMapping
	public Page<Kanji> getKanjis(@RequestParam(required = false, value = "search") final String search,
			Pageable pageable) {
		return kanjiService.getKanjis(search, pageable);
	}

	/**
	 * Saving new kanji
	 * 
	 * @param kanji
	 * 			Kanji business object
	 * @param autodetectReadings
	 * 			Calling external API for auto readings/translations setting (optional)
	 * @return
	 * 			Created kanji
	 */
	@PostMapping
	public Kanji addKanji(@RequestBody Kanji kanji,
			@RequestParam(defaultValue = "true", value = "autoDetectReadings") boolean autoDetect) {
		return kanjiService.addKanji(kanji, autoDetect);
	}
	
	/**
	 * Modify an existing kanji attributes
	 * 
	 * @param kanjiId
	 * 			Tehnical ID of the kanji present in database
	 * @param patchRequest
	 * 			Data which have to be modified
	 * @return
	 * 			Updated Kanji
	 */
	@PatchMapping(path = "/{kanjiId}", consumes = "application/merge-patch+json")
	public Kanji updateKanji(@PathVariable Long kanjiId,
			@RequestBody JsonMergePatchImpl patchRequest) {
		return kanjiService.patchKanji(kanjiId, patchRequest);
	}
}
