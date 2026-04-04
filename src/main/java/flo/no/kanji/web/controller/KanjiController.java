package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;
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
 * Kanji REST Controller
 *
 * @author Florian
 */
@RestController
@RequestMapping("/kanjis")
@RequiredArgsConstructor
@Validated
public class KanjiController {

    /** Kanji business service **/
    private final KanjiService kanjiService;

    /**
     * Find kanji
     *
     * @param kanjiId Kanji database identifier
     * @return Retrieved kanji business object
     */
    @GetMapping("/{kanjiId}")
    public Kanji getKanji(@PathVariable Long kanjiId) {
        return kanjiService.findById(kanjiId);
    }

    /**
     * Search kanjis
     *
     * @param search   Japanese kanji value
     * @param lang     Translation language filter
     * @param pageable Returned page parameters (limit, number of items per page...)
     * @return Spring page of retrieved corresponding kanjis
     */
    @GetMapping
    public Page<Kanji> searchKanjis(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Language lang,
            @ParameterObject @PageableDefault Pageable pageable) {
        return kanjiService.getKanjis(search, lang, pageable);
    }

    /**
     * Saving new kanji
     *
     * @param kanji      Kanji business object
     * @param autoDetect Calling external API for auto readings/translations setting (optional)
     * @param preview    Return unsaved object
     * @return Created kanji
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Kanji addKanji(
            @RequestBody @Valid Kanji kanji,
            @RequestParam(defaultValue = "false") boolean autoDetect,
            @RequestParam(defaultValue = "false") boolean preview) {
        return kanjiService.addKanji(kanji, autoDetect, preview);
    }

    /**
     * Modify an existing kanji attributes
     *
     * @param kanjiId Technical ID of the kanji present in database
     * @param patch   Data which have to be modified
     * @return Updated Kanji
     */
    @PatchMapping("/{kanjiId}")
    public Kanji updateKanji(@PathVariable Long kanjiId, @RequestBody JsonNode patch) {
        return kanjiService.patchKanji(kanjiId, patch);
    }

    /**
     * Delete a kanji
     *
     * @param kanjiId Kanji ID
     */
    @DeleteMapping("/{kanjiId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteKanji(@PathVariable Long kanjiId) {
        kanjiService.deleteKanji(kanjiId);
    }
}
