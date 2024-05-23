package flo.no.kanji.util;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;
import com.moji4j.MojiConverter;
import com.moji4j.MojiDetector;
import flo.no.kanji.business.constants.CharacterType;
import org.springframework.util.ObjectUtils;

import java.lang.Character.UnicodeBlock;
import java.util.stream.Collectors;

import static java.lang.Character.UnicodeBlock.*;

/**
 * Japanese characters class utils
 *
 * @author Florian
 */
public final class CharacterUtils {

    /**
     * Japanese alphabet (hiragana/katakana/kanji) detetor
     **/
    private static final MojiDetector mojiDetector = new MojiDetector();

    /**
     * Japanese characters converter
     **/
    private static final MojiConverter mojiConverter = new MojiConverter();

    /**
     * Japanese characters tokenizer
     **/
    private static final Tokenizer tokenizer = new Tokenizer();

    /**
     * Private constructor : The class provides only static methods so no instantiation needed
     **/
    private CharacterUtils() {
    }

    /**
     * Determines if the input chain is strictly a kanji value
     *
     * @param input Input characters
     * @return true if the input chain is strictly a kanji value, false otherwise
     */
    public static boolean isKanji(String input) {
        return input.chars().allMatch(c -> UnicodeBlock.of(c) == CJK_UNIFIED_IDEOGRAPHS || c == '々');
    }

    /**
     * Determines if the input chain is a japanese word containing kanji(s) and conjugation hiragana(s) (okuriganas)
     *
     * @param input Input characters
     * @return true if the input chain is kanji(s) with conjugation okurigana(s), false otherwise
     */
    public static boolean isKanjiWithOkurigana(String input) {
        return mojiDetector.hasKanji(input) && mojiDetector.hasKana(input) && input.chars().allMatch(c -> {
            final var charType = UnicodeBlock.of(c);
            return charType == HIRAGANA || charType == CJK_UNIFIED_IDEOGRAPHS;
        });
    }

    /**
     * Determines if the input chain is strictly hiragana value
     *
     * @param input Input chain
     * @return true if the input chain is hiragana, false otherwise
     */
    public static boolean isHiragana(String input) {
        return input.chars().allMatch(c -> UnicodeBlock.of(c) == HIRAGANA);
    }

    /**
     * Determines if the input chain is strictly katakana value
     *
     * @param input Input chain
     * @return true if the input chain is katakana, false otherwise
     */
    public static boolean isKatakana(String input) {
        return input.chars().allMatch(c -> UnicodeBlock.of(c) == KATAKANA);
    }

    /**
     * Determines if the input chain is strictly latin value
     *
     * @param input Input chain
     * @return true if the input chain is latin, false otherwise
     */
    public static boolean isRomaji(String input) {
        return input.chars().allMatch(c -> (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
    }

    /**
     * Determines the corresponding japanese alphabet type of input chain
     *
     * @param input String input
     * @return The corresponding japanese alphabet type
     */
    public static CharacterType getCharacterType(String input) {

        CharacterType type = null;
        // Cleaning input from undesirable characters such as -; .
        final var value = clearInput(input);

        if (!ObjectUtils.isEmpty(value)) {
            if (isHiragana(value)) {
                type = CharacterType.HIRAGANA;
            } else if (isKatakana(value)) {
                type = CharacterType.KATAKANA;
            } else if (isKanji(value)) {
                type = CharacterType.KANJI;
            } else if (isKanjiWithOkurigana(value)) {
                type = CharacterType.KANJI_WITH_OKURIGANA;
            } else if (isRomaji(value)) {
                type = CharacterType.ROMAJI;
            }
        }

        return type;
    }

    /**
     * Converts a Kana value (hiragana or katakana) to a standard plain hiragana value
     *
     * @param value Kana string input
     * @return Hiragana converted value
     */
    public static String convertKanaToFurigana(final String value) {
        // Since MojiConverter library doesn't provide a katakana to hiragana converting method we first convert
        // to romaji
        return CharacterUtils.isHiragana(value) ? value :
                mojiConverter.convertRomajiToHiragana(mojiConverter.convertKanaToRomaji(value));
    }

    public static String getWordFurigana(final String value) {
        var tokens = tokenizer.tokenize(value);
        var katakanaValue = tokens.stream()
                .map(Token::getReading)
                .collect(Collectors.joining());
        var furigana = convertKanaToFurigana(katakanaValue);
        return isHiragana(furigana) ? furigana : null;
    }

    public static boolean isJapanese(final String value) {
        return value.chars().mapToObj(c -> (char) c)
                .allMatch(CharacterUtils::isJapaneseCharacter);
    }

    private static boolean isJapaneseCharacter(char ch) {
        UnicodeBlock block = UnicodeBlock.of(ch);
        return block == UnicodeBlock.HIRAGANA ||
                block == UnicodeBlock.KATAKANA ||
                block == UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS ||
                block == UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                block == UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
                block == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS ||
                block == UnicodeBlock.CJK_COMPATIBILITY_FORMS ||
                block == UnicodeBlock.VERTICAL_FORMS;
    }

    /**
     * Cleans input from separating characters
     *
     * @param input Input String
     * @return Cleaned output
     */
    private static String clearInput(final String input) {
        return input.replaceAll("-|（|）|\\.|\\s+", "").trim();
    }
}
