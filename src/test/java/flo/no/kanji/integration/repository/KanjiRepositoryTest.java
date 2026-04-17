package flo.no.kanji.integration.repository;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.specification.KanjiSpecification;
import flo.no.kanji.util.SearchQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * KanjiRepository test class
 *
 * @author Florian
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
public class KanjiRepositoryTest {

    private static final String USER_SUB = "auth0|662dc5e995203229af749169";
    private final MojiConverter converter = new MojiConverter();

    @Autowired
    private KanjiRepository kanjiRepository;

    @Test
    public void searchKanjiByValueTest() {
        var spec = KanjiSpecification.searchKanji(SearchQuery.from("君", converter), null, USER_SUB);
        assertKanjiEquals(kanjiRepository.findAll(spec));
    }

    @Test
    public void searchKanjiByKunYomiTest() {
        var spec = KanjiSpecification.searchKanji(SearchQuery.from("きみ", converter), null, USER_SUB);
        assertKanjiEquals(kanjiRepository.findAll(spec));
    }

    @Test
    public void searchKanjiByOnYomiTest() {
        var spec = KanjiSpecification.searchKanji(SearchQuery.from("クン", converter), null, USER_SUB);
        assertKanjiEquals(kanjiRepository.findAll(spec));
    }

    @Test
    public void searchKanjiByTranslationTest() {
        var spec = KanjiSpecification.searchKanji(SearchQuery.from("mister", converter), null, USER_SUB);
        assertKanjiEquals(kanjiRepository.findAll(spec));
    }

    @Test
    public void searchKanjiByRomajiKunTest() {
        var spec = KanjiSpecification.searchKanji(SearchQuery.from("kimi", converter), null, USER_SUB);
        assertKanjiEquals(kanjiRepository.findAll(spec));
    }

    @Test
    public void searchKanjiByRomajiOnTest() {
        var spec = KanjiSpecification.searchKanji(SearchQuery.from("kun", converter), null, USER_SUB);
        assertKanjiEquals(kanjiRepository.findAll(spec));
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
