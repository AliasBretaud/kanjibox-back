package flo.no.kanji.util;

import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.HIRAGANA;
import static java.lang.Character.UnicodeBlock.KATAKANA;

import java.lang.Character.UnicodeBlock;

import org.springframework.util.ObjectUtils;

import com.moji4j.MojiDetector;

import flo.no.kanji.business.constants.CharacterType;

public class CharacterUtils {
	
	private static MojiDetector mojiDetector = new MojiDetector();

	public static boolean isKanji(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == CJK_UNIFIED_IDEOGRAPHS);
	}
	
	public static boolean isKanjiWithOkurigana(String input) {
		return mojiDetector.hasKanji(input) && mojiDetector.hasKana(input) && !mojiDetector.hasLatin(input);
	}

	public static boolean isHiragana(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == HIRAGANA);
	}

	public static boolean isKatakana(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == KATAKANA);
	}

	public static CharacterType getCharacterType(String input) {

		CharacterType type = null;

		if (!ObjectUtils.isEmpty(input)) {
			if (isHiragana(input)) {
				type = CharacterType.HIRAGANA;
			} else if (isKatakana(input)) {
				type = CharacterType.KATAKANA;
			} else if (isKanji(input)) {
				type = CharacterType.KANJI;
			} else if (isKanjiWithOkurigana(input)){
				type = CharacterType.KANJI_WITH_OKURIGANA;
			} else {
				type = CharacterType.ROMAJI;
			}
		}

		return type;
	}
}
