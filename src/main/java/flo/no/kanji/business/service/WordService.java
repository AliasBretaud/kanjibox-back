package flo.no.kanji.business.service;

import com.fasterxml.jackson.databind.JsonNode;
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
     * @param userSub   Identification of the user searching the words
     * @return Spring page of retrieved corresponding words
     */
    Page<Word> getWords(String search, Language language, Integer listLimit, Pageable pageable, String userSub);

    /**
     * Saving new word
     *
     * @param word    Word business object
     * @param preview Return unsaved value
     * @param userSub Identification of the user adding the word
     * @return Created word
     */
    Word addWord(@Valid Word word, boolean preview, String userSub);

    /**
     * Delete word
     *
     * @param wordId  Word ID
     * @param userSub Identification of the user deleting the word
     */
    void deleteWord(Long wordId, String userSub);

    /**
     * Modify an existing word attributes
     *
     * @param wordId  Technical ID of the word present in database
     * @param patch   Data which have to be modified
     * @param userSub Identification of the user updating the word
     * @return Updated Word
     */
    Word patchWord(Long wordId, JsonNode patch, String userSub);
}
