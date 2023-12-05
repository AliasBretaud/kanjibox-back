package flo.no.kanji.web.controller;

import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Word REST Controller
 * @author Florian
 */
@RestController
@RequestMapping("/words")
public class WordController {

	/** Word business service */
	@Autowired
	private WordService wordService;

	/**
	 * Search words by its japanese value
	 * @param search
	 * 			Word japansese writing value
	 * @param pageable
	 * 			Returned page parameters (limit, number of items per page...)
	 * @return
	 * 			Spring page of retrieved corresponding words
	 */
	@GetMapping
	public Page<Word> getWords(@RequestParam(required = false, value = "search") final String search,
			Pageable pageable) {
		return wordService.getWords(search, pageable != null ? pageable : Pageable.ofSize(10));
	}

	/**
	 * Saving new word
	 * @param word
	 * 			Word business object
	 * @return
	 * 			Created word
	 */
	@PostMapping
	public Word addWord(@RequestBody Word word) {
		return wordService.addWord(word);
	}
}
