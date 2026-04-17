package flo.no.kanji.util;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.business.exception.InvalidInputException;

/**
 * Represents a pre-classified and pre-converted search query.
 * Built once in the service layer so that specifications receive
 * only plain values with no dependency on conversion utilities.
 */
public record SearchQuery(String raw, CharacterType type, String hiragana, String katakana) {

    public static SearchQuery from(String search, MojiConverter converter) {
        var type = CharacterUtils.getCharacterType(search);
        if (type == null) {
            throw new InvalidInputException("Invalid search value format");
        }
        String hiragana = type == CharacterType.ROMAJI ? converter.convertRomajiToHiragana(search) : null;
        String katakana = type == CharacterType.ROMAJI ? converter.convertRomajiToKatakana(search) : null;
        return new SearchQuery(search, type, hiragana, katakana);
    }
}
