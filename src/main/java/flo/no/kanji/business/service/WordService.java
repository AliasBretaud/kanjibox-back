package flo.no.kanji.business.service;

import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Word operations business service
 *
 * @author Florian
 */
public interface WordService {

    /**
     * Search words by its japanese value
     *
     * @param search    Word japansese writing value
     * @param language  Translations language filter
     * @param listLimit Max size of the lists contained in the returned object
     * @param pageable  Returned page parameters (limit, number of items per page...)
     * @return Spring page of retrieved corresponding words
     */
    Page<Word> getWords(String search, Language language, Integer listLimit, Pageable pageable);

    /**
     * Saving new word
     *
     * @param word    Word business object
     * @param preview Return unsaved value
     * @return Created word
     */
    Word addWord(@Valid Word word, boolean preview);
}
