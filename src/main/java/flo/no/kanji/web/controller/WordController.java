package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Word REST Controller
 *
 * @author Florian
 */
@RestController
@RequestMapping("/words")
@RequiredArgsConstructor
@Validated
public class WordController {

    /** Word business service */
    private final WordService wordService;

    /**
     * Search words by its japanese value
     *
     * @param search    Word japanese writing value
     * @param lang      Translation language filter
     * @param listLimit Max size of the lists contained in the returned object
     * @param pageable  Returned page parameters (limit, number of items per page...)
     * @return Spring page of retrieved corresponding words
     */
    @GetMapping
    public Page<Word> getWords(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Language lang,
            @RequestParam(required = false) Integer listLimit,
            @ParameterObject @PageableDefault Pageable pageable) {
        return wordService.getWords(search, lang, listLimit, pageable);
    }

    /**
     * Saving new word
     *
     * @param word    Word business object
     * @param preview Return unsaved object
     * @return Created word
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Word addWord(
            @RequestBody @Valid Word word,
            @RequestParam(defaultValue = "false") boolean preview) {
        return wordService.addWord(word, preview);
    }

    /**
     * Modify an existing word attributes
     *
     * @param wordId Technical ID of the word present in database
     * @param patch  Data which have to be modified
     * @return Updated Word
     */
    @PatchMapping("/{wordId}")
    public Word updateWord(@PathVariable Long wordId, @RequestBody JsonNode patch) {
        return wordService.patchWord(wordId, patch);
    }

    /**
     * Delete a word
     *
     * @param wordId Word ID
     */
    @DeleteMapping("/{wordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWord(@PathVariable Long wordId) {
        wordService.deleteWord(wordId);
    }
}
