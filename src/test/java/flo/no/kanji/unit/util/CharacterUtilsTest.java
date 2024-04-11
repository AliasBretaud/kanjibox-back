package flo.no.kanji.unit.util;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.util.CharacterUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterUtilsTest {

    @Test
    public void getCharacterTypeTest() {
        var value = "あ";
        assertEquals(CharacterType.HIRAGANA, CharacterUtils.getCharacterType(value));
    }

    @Test
    public void getCharacterTypeTest2() {
        var value = "ア";
        assertEquals(CharacterType.KATAKANA, CharacterUtils.getCharacterType(value));
    }

    @Test
    public void getCharacterTypeTest3() {
        var value = "亜";
        assertEquals(CharacterType.KANJI, CharacterUtils.getCharacterType(value));
    }

    @Test
    public void getCharacterTypeTest4() {
        var value = "会う";
        assertEquals(CharacterType.KANJI_WITH_OKURIGANA, CharacterUtils.getCharacterType(value));
    }

    @Test
    public void getCharacterTypeTest5() {
        var value = "abz";
        assertEquals(CharacterType.ROMAJI, CharacterUtils.getCharacterType(value));
        value = "ABZ";
        assertEquals(CharacterType.ROMAJI, CharacterUtils.getCharacterType(value));
    }

    @Test
    public void getCharacterTypeTest6() {
        var value = "+";
        assertNull(CharacterUtils.getCharacterType(value));
    }

    @Test
    public void getCharacterTypeTest7() {
        var value = "";
        assertNull(CharacterUtils.getCharacterType(value));
    }

    @Test
    public void isOkuriganaTestKo() {
        var value = "あう";
        assertFalse(CharacterUtils.isKanjiWithOkurigana(value));
        value = "アウト";
        assertFalse(CharacterUtils.isKanjiWithOkurigana(value));
        value = "火山";
        assertFalse(CharacterUtils.isKanjiWithOkurigana(value));
        value = "火ウ";
        assertFalse(CharacterUtils.isKanjiWithOkurigana(value));
        value = "aa";
        assertFalse(CharacterUtils.isKanjiWithOkurigana(value));
    }

    @Test
    public void isRomajiTestKo() {
        var value = "aあ";
        assertFalse(CharacterUtils.isRomaji(value));
    }

}

