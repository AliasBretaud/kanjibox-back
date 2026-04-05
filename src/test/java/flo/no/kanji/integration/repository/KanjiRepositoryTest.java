package flo.no.kanji.integration.repository;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.specification.KanjiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * KankiRepository test class
 *
 * @author Florian
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class KanjiRepositoryTest {

    private final MojiConverter converter = new MojiConverter();
    /** Test class **/
    @Autowired
    private KanjiRepository kanjiRepository;

    @BeforeEach
    public void setUp() {
    }

    /**
     *
     */
    @Test
    public void searchKanjiByValueTest() {
        // PREPARE
        var spec = KanjiSpecification.searchKanji("君", null, converter, "auth0|662dc5e995203229af749169");
        // EXECUTE
        var kanjis = kanjiRepository.findAll(spec);
        // ASSERT
        assertKanjiEquals(kanjis);
    }

    @Test
    public void searchKanjiByKunYomiTest() {
        // PREPARE
        var spec = KanjiSpecification.searchKanji("きみ", null, converter, "auth0|662dc5e995203229af749169");
        // EXECUTE
        var kanjis = kanjiRepository.findAll(spec);
        // ASSERT
        assertKanjiEquals(kanjis);
    }

    @Test
    public void searchKanjiByOnYomiTest() {
        // PREPARE
        var spec = KanjiSpecification.searchKanji("クン", null, converter, "auth0|662dc5e995203229af749169");
        // EXECUTE
        var kanjis = kanjiRepository.findAll(spec);
        // ASSERT
        assertKanjiEquals(kanjis);
    }

    @Test
    public void searchKanjiByTranslationTest() {
        // PREPARE
        var spec = KanjiSpecification.searchKanji("mister", null, converter, "auth0|662dc5e995203229af749169");
        // EXECUTE
        var kanjis = kanjiRepository.findAll(spec);
        // ASSERT
        assertKanjiEquals(kanjis);
    }

    @Test
    public void searchKanjiByRomajiKunTest() {
        // PREPARE
        var spec = KanjiSpecification.searchKanji("kimi", null, converter, "auth0|662dc5e995203229af749169");
        // EXECUTE
        var kanjis = kanjiRepository.findAll(spec);
        // ASSERT
        assertKanjiEquals(kanjis);
    }

    @Test
    public void searchKanjiByRomajiOnTest() {
        // PREPARE
        var spec = KanjiSpecification.searchKanji("kun", null, converter, "auth0|662dc5e995203229af749169");
        // EXECUTE
        var kanjis = kanjiRepository.findAll(spec);
        // ASSERT
        assertKanjiEquals(kanjis);
    }

    private void assertKanjiEquals(final List<KanjiEntity> kanjis) {
        assertEquals(1, kanjis.size());
        var kanji = kanjis.getFirst();
        assertEquals("君", kanji.getValue());
        assertEquals(List.of("きみ", "-ぎみ"), kanji.getKunYomi());
        assertEquals(List.of("クン"), kanji.getOnYomi());
        assertEquals(
                List.of("mister", "you", "ruler", "male name suffix"),
                kanji.getTranslations().stream()
                        .filter(t -> t.getLanguage().equals((Language.EN)))
                        .map(TranslationEntity::getTranslation).toList());
        assertEquals(LocalDateTime.of(2020, 4, 14, 1, 21, 52), kanji.getTimeStamp());
    }

}
