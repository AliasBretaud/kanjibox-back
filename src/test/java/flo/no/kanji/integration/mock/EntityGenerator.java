package flo.no.kanji.integration.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flo.no.kanji.integration.entity.KanjiEntity;

/**
 * Mock class generating dummy entities
 * @author Florian
 */
public class EntityGenerator {

	/** Object map containing generated test entities **/
	private static Map<String, Object> objectMap = new HashMap<>();
	
	/**
	 * Generating KanjiEntity dummy object
	 * @return KanjiEntity
	 */
	public static KanjiEntity getKanjiEntity() {
		if (!objectMap.containsKey("kanjiEntity")) {
			var kanjiEntity = KanjiEntity.builder()
					.id(1L)
					.value("人")
					.kunYomi(List.of("ひと"))
					.onYomi(List.of("ジン"))
					.translations(List.of("People"))
					.build();
			objectMap.put("kanjiEntity", kanjiEntity);
		}
		return (KanjiEntity) objectMap.get("kanjiEntity");
	}
}
