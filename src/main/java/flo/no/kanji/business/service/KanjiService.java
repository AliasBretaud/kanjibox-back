package flo.no.kanji.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Kanji;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Kanji operations business service
 *
 * @author Florian
 */
public interface KanjiService {

    /**
     * Saving new kanji
     *
     * @param kanji      Kanji business object
     * @param autoDetect Calling external API for auto readings/translations setting (optional)
     * @param preview    Return the unsaved object
     * @param userSub    Identification of the user adding the kanji
     * @return Created kanji
     */
    Kanji addKanji(@Valid Kanji kanji, boolean autoDetect, boolean preview, String userSub);

    /**
     * Merges the existing kanji translations and fills the ones not provided
     *
     * @param kanji Kanji
     * @return The built list of all translations
     */
    Map<Language, List<String>> buildTranslations(Kanji kanji);

    /**
     * Affecting auto-determined readings and translations to an existing kanji
     *
     * @param kanji Existing kanji business object
     */
    void autoFillKanjiReadigs(Kanji kanji);

    /**
     * Search kanjis
     *
     * @param search   Japanese kanji value
     * @param language Filter for translations language
     * @param pageable Returned page parameters (limit, number of items per page...)
     * @param userSub  Identification of the user searching the kanjis
     * @return Spring page of retrieved corresponding kanjis
     */
    Page<Kanji> getKanjis(String search, Language language, Pageable pageable, String userSub);

    /**
     * Modify an existing kanji attributes
     *
     * @param kanjiId Technical ID of the kanji present in database
     * @param patch   Data which have to be modified
     * @param userSub Identification of the user updating the kanji
     * @return Updated Kanji
     */
    Kanji patchKanji(Long kanjiId, JsonNode patch, String userSub);

    /**
     * Find a single unit kanji by its ID
     *
     * @param kanjiId Kanji ID
     * @param userSub Identification of the user finding the kanji
     * @return Kanji entity returned from database
     */
    Kanji findById(Long kanjiId, String userSub);

    /**
     * Delete a kanji
     *
     * @param kanjiId Kanji ID
     * @param userSub Identification of the user deleting the kanji
     */
    void deleteKanji(Long kanjiId, String userSub);
}
