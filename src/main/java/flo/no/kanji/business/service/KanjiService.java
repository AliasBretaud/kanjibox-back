package flo.no.kanji.business.service;

import javax.json.JsonMergePatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;

/**
 * Kanji operations business service
 * 
 * @author Florian
 *
 */
public interface KanjiService {

	/**
	 * Saving new kanji
	 * 
	 * @param kanji
	 * 			Kanji business object
	 * @param autodetectReadings
	 * 			Calling external API for auto readings/translations seting (optional)
	 * @return
	 * 			Created kanji
	 */
	Kanji addKanji(Kanji kanji, boolean autodetectReadings);

	/**
	 * Seach Kanji by its japanese value
	 * 
	 * @param kanjiValue
	 * 			Kanji japansese writing value
	 * @return
	 * 			Finded kanji
	 */
	Kanji findKanjiByValue(String kanjiValue);

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
	 * 			Tehnical ID of the kanji persent in database
	 * @param patchRequest
	 * 			Data which have to be modified
	 * @return
	 * 			Updated Kanji
	 */
	Kanji patchKanji(Long kanjiId, JsonMergePatch patchRequest);

	/**
	 * Find a single unit kanji by its value
	 * 
	 * @param value
	 * 			Japanese kanji value
	 * @return
	 * 			Kanji entity returned from database
	 */
	KanjiEntity findByValue(String value);
}
