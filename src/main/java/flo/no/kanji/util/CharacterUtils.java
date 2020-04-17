package flo.no.kanji.util;

import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.HIRAGANA;
import static java.lang.Character.UnicodeBlock.KATAKANA;

import java.lang.Character.UnicodeBlock;

public class CharacterUtils {

	public static boolean isKanji(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == CJK_UNIFIED_IDEOGRAPHS);
	}

	public static boolean isHiragana(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == HIRAGANA);
	}

	public static boolean isKatakana(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == KATAKANA);
	}
}
