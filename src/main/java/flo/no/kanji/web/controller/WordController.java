package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.WordService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Word REST Controller
 *
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
     *
     * @param search    Word japansese writing value
     * @param listLimit Max size of the lists contained in the returned object
     * @param pageable  Returned page parameters (limit, number of items per page...)
     * @return Spring page of retrieved corresponding words
     */
    @GetMapping
    public Page<Word> getWords(@RequestParam(required = false, value = "search") final String search,
                               @RequestParam(required = false, value = "lang") final Language lang,
                               @RequestParam(required = false, value = "listLimit") final Integer listLimit,
                               @ParameterObject @PageableDefault final Pageable pageable) {
        return wordService.getWords(search, lang, listLimit, pageable);
    }

    /**
     * Saving new word
     *
     * @param word Word business object
     * @return Created word
     */
    @PostMapping
    public Word addWord(@RequestBody Word word,
                        @RequestParam(defaultValue = "false", value = "preview") boolean preview) {
        return wordService.addWord(word, preview);
    }

    /**
     * Modify an existing word attributes
     *
     * @param wordId Technical ID of the word present in database
     * @param patch  Data which have to be modified
     * @return Updated Word
     */
    @PatchMapping(path = "/{wordId}")
    public Word updateWord(@PathVariable Long wordId,
                           @RequestBody JsonNode patch) {
        return wordService.patchWord(wordId, patch);
    }

    /**
     * Delete a word
     *
     * @param wordId Word ID
     */
    @DeleteMapping(path = "/{wordId}")
    public void deleteWord(@PathVariable("wordId") final Long wordId) {
        wordService.deleteWord(wordId);
    }
}
