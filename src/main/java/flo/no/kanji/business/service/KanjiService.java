package flo.no.kanji.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Kanji operations business service
 * @author Florian
 */
public interface KanjiService {

	/**
	 * Saving new kanji
	 * 
	 * @param kanji
	 * 			Kanji business object
	 * @param autodetectReadings
	 * 			Calling external API for auto readings/translations setting (optional)
	 * @return
	 * 			Created kanji
	 */
	Kanji addKanji(@Valid Kanji kanji, boolean autodetectReadings);

	/**
	 * Affecting auto-determined readings and translations to an existing kanji
	 * 
	 * @param kanji
	 * 			Existing kanji business object
	 */
	void autoFillKanjiReadigs(Kanji kanji);

	/**
	 * Search kanjis
	 * 
	 * @param search
	 * 			Japanese kanji value
	 * @param pageable
	 * 			Returned page parameters (limit, number of items per page...)
	 * @return
	 * 			Spring page of retrieved corresponding kanjis
	 */
	Page<Kanji> getKanjis(String search, Pageable pageable);

	/**
	 * Modify an existing kanji attributes
	 * 
	 * @param kanjiId
	 * 			Technical ID of the kanji persent in database
	 * @param patch
	 * 			Data which have to be modified
	 * @return
	 * 			Updated Kanji
	 */
	Kanji patchKanji(Long kanjiId, JsonNode patch);

	/**
	 * Find a single unit kanji by its value
	 * 
	 * @param value
	 * 			Japanese kanji value
	 * @return
	 * 			Kanji entity returned from database
	 */
	KanjiEntity findByValue(String value);

	/**
	 * Find a single unit kanji by its ID
	 *
	 * @param kanjiId
	 * 			Kanji ID
	 * @return
	 * 			Kanji entity returned from database
	 */
	Kanji findById(Long kanjiId);
}
