package flo.no.kanji.unit.business.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;

/**
 * Mock class generating dummy business objects
 * @author Florian
 *
 */
public class BusinessObjectGenerator {
	
	private static final Map<String, Object> objectMap = new HashMap<>();

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
	
	public static Word getWord() {
		if (!objectMap.containsKey("word")) {
			var word = new Word();
			word.setId(1L);
			word.setValue("火山");
			word.setFuriganaValue("かざん");
			word.setTranslation("Volcano");
			objectMap.put("word", word);
		}
		
		return (Word) objectMap.get("word");
	}
	
	public static void resetKanji() {
		objectMap.remove("kanji");
	}
}
