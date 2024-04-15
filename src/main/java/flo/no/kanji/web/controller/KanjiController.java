package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * Kanji REST Controller
 *
 * @author Florian
 */
@RestController
@RequestMapping("/kanjis")
public class KanjiController {

    /** Kanji business service **/
    @Autowired
    private KanjiService kanjiService;

    /**
     * Find kanji
     *
     * @param kanjiId Kanji database identifier
     * @return Retrieved kanji business object
     */
    @GetMapping(path = "/{kanjiId}")
    public Kanji getKanji(@PathVariable("kanjiId") final Long kanjiId) {
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
    public Page<Kanji> searchKanjis(@RequestParam(required = false, value = "search") final String search,
                                    @RequestParam(required = false, value = "lang") final Language lang,
                                    @ParameterObject @PageableDefault final Pageable pageable) {
        return kanjiService.getKanjis(search, lang, pageable);
    }

    /**
     * Saving new kanji
     *
     * @param kanji      Kanji business object
     * @param autoDetect Calling external API for auto readings/translations setting (optional)
     * @return Created kanji
     */
    @PostMapping
    public Kanji addKanji(@RequestBody Kanji kanji,
                          @RequestParam(defaultValue = "false", value = "autoDetectReadings") boolean autoDetect) {
        return kanjiService.addKanji(kanji, autoDetect);
    }

    /**
     * Modify an existing kanji attributes
     *
     * @param kanjiId Technical ID of the kanji present in database
     * @param patch   Data which have to be modified
     * @return Updated Kanji
     */
    @PatchMapping(path = "/{kanjiId}")
    public Kanji updateKanji(@PathVariable Long kanjiId,
                             @RequestBody JsonNode patch) {
        return kanjiService.patchKanji(kanjiId, patch);
    }
}
