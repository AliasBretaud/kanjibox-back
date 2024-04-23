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
     * @param search    Japanese kanji value
     * @param lang      Translation language filter
     * @param listLimit Max size of the lists contained in the returned object
     * @param pageable  Returned page parameters (limit, number of items per page...)
     * @return Spring page of retrieved corresponding kanjis
     */
    @GetMapping
    public Page<Kanji> searchKanjis(@RequestParam(required = false, value = "search") final String search,
                                    @RequestParam(required = false, value = "lang") final Language lang,
                                    @RequestParam(required = false, value = "listLimit") final Integer listLimit,
                                    @ParameterObject @PageableDefault final Pageable pageable) {
        return kanjiService.getKanjis(search, lang, listLimit, pageable);
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
    public Kanji addKanji(@RequestBody Kanji kanji,
                          @RequestParam(defaultValue = "false", value = "autoDetect") boolean autoDetect,
                          @RequestParam(defaultValue = "false", value = "preview") boolean preview) {
        return kanjiService.addKanji(kanji, autoDetect, preview);
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
