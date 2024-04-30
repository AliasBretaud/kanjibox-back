package flo.no.kanji.unit.util;

import flo.no.kanji.business.constants.CharacterType;
import flo.no.kanji.util.CharacterUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class CharacterUtilsTest {

    @Test
    public void getCharacterTypeTestOk() {
        assertEquals(CharacterType.HIRAGANA, CharacterUtils.getCharacterType("あ"));
        assertEquals(CharacterType.KATAKANA, CharacterUtils.getCharacterType("ア"));
        assertEquals(CharacterType.KANJI, CharacterUtils.getCharacterType("亜"));
        assertEquals(CharacterType.KANJI_WITH_OKURIGANA, CharacterUtils.getCharacterType("会う"));
        assertEquals(CharacterType.ROMAJI, CharacterUtils.getCharacterType("abz"));
    }

    @Test
    public void getCharacterTypeTestKo() {
        assertNull(CharacterUtils.getCharacterType(""));
        assertNull(CharacterUtils.getCharacterType("+"));
    }

    @Test
    public void isOkuriganaTest() {
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
        assertTrue(CharacterUtils.isRomaji("abcd"));
        assertFalse(CharacterUtils.isRomaji("aあ"));
    }

    @Test
    public void convertToFuriganaTest() {
        assertEquals("ことば", CharacterUtils.getWordFurigana("言葉"));
        assertEquals("まるのうち", CharacterUtils.getWordFurigana("丸の内"));
        assertEquals("さまざま", CharacterUtils.getWordFurigana("様々"));
        assertNull(CharacterUtils.getWordFurigana("油淋鶏"));
        assertNull(CharacterUtils.getWordFurigana("ad*df"));
    }
}

