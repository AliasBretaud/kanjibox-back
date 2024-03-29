package flo.no.kanji.integration.mock;

import flo.no.kanji.business.constants.Language;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.WordEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock class generating dummy entities
 * @author Florian
 */
public class EntityGenerator {

	/** Object map containing generated test entities **/
	private static final Map<String, Object> objectMap = new HashMap<>();
	
	/**
	 * Generating KanjiEntity dummy object
	 * @return KanjiEntity
	 */
	public static KanjiEntity getKanjiEntity() {
		var key = "kanjiEntity";
		if (!objectMap.containsKey(key)) {
			var kanjiEntity = KanjiEntity.builder()
					.id(1L)
					.value("人")
					.kunYomi(List.of("ひと"))
					.onYomi(List.of("ジン"))
					.translations(List.of(new TranslationEntity("People", Language.EN)))
					.timeStamp(LocalDateTime.now())
					.build();
			objectMap.put(key, kanjiEntity);
		}
		return (KanjiEntity) objectMap.get(key);
	}
	
	/**
	 * Generating WordEntity dummy object
	 * @return WordEntity
	 */
	public static WordEntity getWordEntity() {
		var key = "wordEntity";
		if (!objectMap.containsKey(key)) {
			var wordEntity = WordEntity.builder()
					.id(1L)
					.value("火山")
					.furiganaValue("かざん")
					.translations(List.of(new TranslationEntity("Volcano", Language.EN)))
					.timeStamp(LocalDateTime.now())
					.build();
			objectMap.put(key, wordEntity);
		}
		return (WordEntity) objectMap.get(key);
	}
}
