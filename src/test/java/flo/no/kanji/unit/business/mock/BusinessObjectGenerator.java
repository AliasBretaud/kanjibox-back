package flo.no.kanji.unit.business.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flo.no.kanji.business.model.Kanji;

/**
 * Mock class generating dummy business objects
 * @author Florian
 *
 */
public class BusinessObjectGenerator {
	
	private static Map<String, Object> objectMap = new HashMap<>();

	public static Kanji getKanji() {
		if (!objectMap.containsKey("kanji")) {
			var kanji = new Kanji();
			kanji.setId(1L);
			kanji.setValue("人");
			kanji.setKunYomi(List.of("ひと"));
			kanji.setOnYomi(List.of("ジン"));
			kanji.setTranslations(List.of("People"));
			objectMap.put("kanji", kanji);
		}
		
		return (Kanji) objectMap.get("kanji");
	}
	
	public static void resetKanji() {
		objectMap.remove("kanji");
	}
}
