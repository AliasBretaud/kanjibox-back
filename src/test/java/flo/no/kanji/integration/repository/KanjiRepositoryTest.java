package flo.no.kanji.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.specification.KanjiSpecification;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class KanjiRepositoryTest {

	@Autowired
	private KanjiRepository kanjiRepository;
	
	@Test
	public void searchKanjiByValueTest() {
		// PREPARE
		var spec = KanjiSpecification.getSearchKanjiSpecification("君");
		// EXECUTE
		var kanjis = kanjiRepository.findAll(spec);
		// ASSERT
		assertKanjiEquals(kanjis);
	}
	
	@Test
	public void searchKanjiByKunYomiTest() {
		// PREPARE
		var spec = KanjiSpecification.getSearchKanjiSpecification("きみ");
		// EXECUTE
		var kanjis = kanjiRepository.findAll(spec);
		// ASSERT
		assertKanjiEquals(kanjis);
	}
	
	@Test
	public void searchKanjiByOnYomiTest() {
		// PREPARE
		var spec = KanjiSpecification.getSearchKanjiSpecification("クン");
		// EXECUTE
		var kanjis = kanjiRepository.findAll(spec);
		// ASSERT
		assertKanjiEquals(kanjis);
	}
	
	@Test
	public void searchKanjiByTranslationTest() {
		// PREPARE
		var spec = KanjiSpecification.getSearchKanjiSpecification("mister");
		// EXECUTE
		var kanjis = kanjiRepository.findAll(spec);
		// ASSERT
		assertKanjiEquals(kanjis);
	}
	
	@Test
	public void searchKanjiByRomajiKunTest() {
		// PREPARE
		var spec = KanjiSpecification.getSearchKanjiSpecification("kimi");
		// EXECUTE
		var kanjis = kanjiRepository.findAll(spec);
		// ASSERT
		assertKanjiEquals(kanjis);
	}
	
	@Test
	public void searchKanjiByRomajiOnTest() {
		// PREPARE
		var spec = KanjiSpecification.getSearchKanjiSpecification("kun");
		// EXECUTE
		var kanjis = kanjiRepository.findAll(spec);
		// ASSERT
		assertKanjiEquals(kanjis);
	}
	
	private void assertKanjiEquals(final List<KanjiEntity> kanjis) {
		assertEquals(1, kanjis.size());
		var kanji = kanjis.get(0);
		assertEquals("君", kanji.getValue());
		assertEquals(List.of("きみ", "-ぎみ"), kanji.getKunYomi());
		assertEquals(List.of("クン"), kanji.getOnYomi());
		assertEquals(List.of("mister", "you", "ruler", "male name suffix"), kanji.getTranslations());
		assertEquals(LocalDateTime.of(2020, 4, 14, 1, 21, 52), kanji.getTimeStamp());
	}
	
}
