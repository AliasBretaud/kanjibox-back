package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
@Tag(name = "Word", description = "Endpoints for managing Words and their translations")
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
     * @param principal Authenticated user
     * @return Spring page of retrieved corresponding words
     */
    @GetMapping
    @Operation(summary = "Search words", description = "Search for words in the user's collection with optional filters.")
    public Page<Word> getWords(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Language lang,
            @RequestParam(required = false) Integer listLimit,
            @ParameterObject @PageableDefault Pageable pageable,
            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return wordService.getWords(search, lang, listLimit, pageable, principal.getName());
    }

    /**
     * Saving new word
     *
     * @param word      Word business object
     * @param preview   Return unsaved object
     * @param principal Authenticated user
     * @return Created word
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new word", description = "Creates a new word in the user's collection.")
    @ApiResponse(responseCode = "201", description = "Word created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or Word already exists")
    public Word addWord(
            @RequestBody @Valid Word word,
            @RequestParam(defaultValue = "false") boolean preview,
            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return wordService.addWord(word, preview, principal.getName());
    }

    /**
     * Modify an existing word attributes
     *
     * @param wordId    Technical ID of the word present in database
     * @param patch     Data which have to be modified
     * @param principal Authenticated user
     * @return Updated Word
     */
    @PatchMapping("/{wordId}")
    @Operation(summary = "Patch a word", description = "Partially updates an existing word using a JSON patch.")
    public Word updateWord(@PathVariable Long wordId, @RequestBody JsonNode patch, @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return wordService.patchWord(wordId, patch, principal.getName());
    }

    /**
     * Delete a word
     *
     * @param wordId    Word ID
     * @param principal Authenticated user
     */
    @DeleteMapping("/{wordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a word", description = "Removes a word from the user's collection.")
    @ApiResponse(responseCode = "204", description = "Word deleted successfully")
    public void deleteWord(@PathVariable Long wordId, @Parameter(hidden = true) JwtAuthenticationToken principal) {
        wordService.deleteWord(wordId, principal.getName());
    }
}
