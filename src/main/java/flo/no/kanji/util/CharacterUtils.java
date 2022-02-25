package flo.no.kanji.util;

import static java.lang.Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
import static java.lang.Character.UnicodeBlock.HIRAGANA;
import static java.lang.Character.UnicodeBlock.KATAKANA;

import java.lang.Character.UnicodeBlock;

import org.springframework.util.ObjectUtils;

import com.moji4j.MojiDetector;

import flo.no.kanji.business.constants.CharacterType;

/**
 * Japanese characters class utils
 * 
 * @author Florian
 *
 */
public class CharacterUtils {
	
	/** Japanese alphabet (hiragana/katakana/kanji) detetor **/
	private static MojiDetector mojiDetector = new MojiDetector();

	/**
	 * Determines if the input chain is strictly a kanji value
	 * 
	 * @param input
	 * 			Input characters
	 * @return
	 * 			true if the input chain is strictly a kanji value, false otherwise
	 */
	public static boolean isKanji(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == CJK_UNIFIED_IDEOGRAPHS);
	}
	
	/**
	 * Determines if the input chain is a japanese word containing kanji(s) and conjugation hiragana(s) (okuriganas)
	 * 
	 * @param input
	 * 			Input characters
	 * @return
	 * 			true if the input chain is kanji(s) with conjugation okurigana(s), false otherwise
	 */
	public static boolean isKanjiWithOkurigana(String input) {
		return mojiDetector.hasKanji(input) && mojiDetector.hasKana(input) && !mojiDetector.hasLatin(input);
	}

	/**
	 * Determines if the input chain is strictly hiragana value
	 * 
	 * @param input
	 * 			Input chain
	 * @return
	 * 			true if the input chain is hiragana, false otherwise
	 */
	public static boolean isHiragana(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == HIRAGANA);
	}

	/**
	 * Determines if the input chain is strictly katakana value
	 * 
	 * @param input
	 * 			Input chain
	 * @return
	 * 			true if the input chain is katakana, false otherwise
	 */
	public static boolean isKatakana(String input) {
		return input.chars().allMatch(c -> UnicodeBlock.of(c) == KATAKANA);
	}

	/**
	 * Determines the corresponding japanese alphabet type of input chain
	 * 
	 * @param input
	 * 			String input
	 * @return
	 * 		The corresponding japanese alphabet type
	 */
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
