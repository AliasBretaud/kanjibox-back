package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.service.WordService;
import flo.no.kanji.web.dto.WordRequest;
import flo.no.kanji.web.dto.WordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final WordService wordService;
    private final WordMapper wordMapper;

    @GetMapping
    @Operation(summary = "Search words", description = "Search for words in the user's collection with optional filters.")
    public Page<WordResponse> getWords(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Language lang,
            @RequestParam(required = false) Integer listLimit,
            @ParameterObject @PageableDefault Pageable pageable,
            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return wordService.getWords(search, lang, listLimit, pageable, principal.getName())
                .map(wordMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new word", description = "Creates a new word in the user's collection.")
    @ApiResponse(responseCode = "201", description = "Word created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or Word already exists")
    public WordResponse addWord(
            @RequestBody @Valid WordRequest request,
            @RequestParam(defaultValue = "false") boolean preview,
            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return wordMapper.toResponse(
                wordService.addWord(wordMapper.toDomain(request), preview, principal.getName()));
    }

    @PatchMapping("/{wordId}")
    @Operation(summary = "Patch a word", description = "Partially updates an existing word using a JSON patch.")
    public WordResponse updateWord(@PathVariable Long wordId,
                                   @RequestBody JsonNode patch,
                                   @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return wordMapper.toResponse(wordService.patchWord(wordId, patch, principal.getName()));
    }

    @DeleteMapping("/{wordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a word", description = "Removes a word from the user's collection.")
    @ApiResponse(responseCode = "204", description = "Word deleted successfully")
    public void deleteWord(@PathVariable Long wordId,
                           @Parameter(hidden = true) JwtAuthenticationToken principal) {
        wordService.deleteWord(wordId, principal.getName());
    }
}
