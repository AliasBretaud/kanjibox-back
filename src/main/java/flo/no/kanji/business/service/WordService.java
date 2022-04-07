package flo.no.kanji.business.service;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import flo.no.kanji.business.model.Word;

/**
 * Word operations business service
 * @author Florian
 */
public interface WordService {

	/**
	 * Search words by its japanese value
	 * @param search
	 * 			Word japansese writing value
	 * @param pageable
	 * 			Returned page parameters (limit, number of items per page...)
	 * @return
	 * 			Spring page of retrieved corresponding words
	 */
	public Page<Word> getWords(String search, Pageable pageable);

	/**
	 * Saving new word
	 * @param word
	 * 			Word business object
	 * @return
	 * 			Created word
	 */
	public Word addWord(@Valid Word word);
}
