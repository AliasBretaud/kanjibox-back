package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.web.dto.KanjiRequest;
import flo.no.kanji.web.dto.KanjiResponse;
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
 * Kanji REST Controller
 *
 * @author Florian
 */
@RestController
@RequestMapping("/kanjis")
@RequiredArgsConstructor
@Validated
@Tag(name = "Kanji", description = "Endpoints for managing Kanjis and their translations")
public class KanjiController {

    private final KanjiService kanjiService;
    private final KanjiMapper kanjiMapper;

    @GetMapping("/{kanjiId}")
    @Operation(summary = "Get a kanji by its ID", description = "Retrieves a specific kanji from the user's collection.")
    @ApiResponse(responseCode = "200", description = "Found the kanji")
    @ApiResponse(responseCode = "404", description = "Kanji not found")
    public KanjiResponse getKanji(@PathVariable Long kanjiId,
                                  @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return kanjiMapper.toResponse(kanjiService.findById(kanjiId, principal.getName()));
    }

    @GetMapping
    @Operation(summary = "Search kanjis", description = "Search for kanjis in the user's collection with optional filters.")
    public Page<KanjiResponse> searchKanjis(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Language lang,
            @ParameterObject @PageableDefault Pageable pageable,
            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return kanjiService.getKanjis(search, lang, pageable, principal.getName())
                .map(kanjiMapper::toResponse);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new kanji", description = "Creates a new kanji in the user's collection, with optional automatic reading and translation detection.")
    @ApiResponse(responseCode = "201", description = "Kanji created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or Kanji already exists")
    public KanjiResponse addKanji(
            @RequestBody @Valid KanjiRequest request,
            @RequestParam(defaultValue = "false") boolean autoDetect,
            @RequestParam(defaultValue = "false") boolean preview,
            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return kanjiMapper.toResponse(
                kanjiService.addKanji(kanjiMapper.toDomain(request), autoDetect, preview, principal.getName()));
    }

    @PatchMapping("/{kanjiId}")
    @Operation(summary = "Patch a kanji", description = "Partially updates an existing kanji using a JSON patch.")
    public KanjiResponse updateKanji(@PathVariable Long kanjiId,
                                     @RequestBody JsonNode patch,
                                     @Parameter(hidden = true) JwtAuthenticationToken principal) {
        return kanjiMapper.toResponse(kanjiService.patchKanji(kanjiId, patch, principal.getName()));
    }

    @DeleteMapping("/{kanjiId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a kanji", description = "Removes a kanji from the user's collection.")
    @ApiResponse(responseCode = "204", description = "Kanji deleted successfully")
    public void deleteKanji(@PathVariable Long kanjiId,
                            @Parameter(hidden = true) JwtAuthenticationToken principal) {
        kanjiService.deleteKanji(kanjiId, principal.getName());
    }
}
