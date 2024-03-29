package flo.no.kanji.unit.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.impl.KanjiServiceImpl;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.mock.EntityGenerator;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.unit.business.mock.BusinessObjectGenerator;
import flo.no.kanji.util.PatchHelper;
import flo.no.kanji.web.api.KanjiApiClient;
import flo.no.kanji.web.api.model.KanjiVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KanjiServiceTest {

	@Spy
	private KanjiMapper kanjiMapper = Mockito.spy(KanjiMapper.class);

	@Mock
	private KanjiRepository kanjiRepository;

	@Mock
	private KanjiApiClient kanjiApiClient;
	
	@Mock
	private PatchHelper patchHelper;
	
	@Spy
	private MojiConverter mojiConverter;

	@InjectMocks
	@Spy
	private KanjiServiceImpl kanjiServiceImpl;
	
	@Test
	public void addKanjiTestOk1() {
		// PREPARE
		when(kanjiRepository.save(any(KanjiEntity.class)))
			.thenReturn(EntityGenerator.getKanjiEntity());
		var kanji = BusinessObjectGenerator.getKanji();
		//EXECUTE
		var created = kanjiServiceImpl.addKanji(kanji, false);
		// ASSERT
		assertEquals(kanji, created);
	}
	
	@Test
	public void addKanjiTestOk2() {
		// PREPARE
		when(kanjiRepository.save(any(KanjiEntity.class)))
			.thenReturn(EntityGenerator.getKanjiEntity());
		var kanji = BusinessObjectGenerator.getKanji();
		//EXECUTE
		var created = kanjiServiceImpl.addKanji(kanji, true);
		BusinessObjectGenerator.resetKanji();
		// ASSERT
		assertEquals(kanji, created);
		verify(kanjiServiceImpl, times(1)).autoFillKanjiReadigs(any(Kanji.class));
	}
	
	@Test
	public void addKanjiTestKo() {
		// PREPARE
		when(kanjiRepository.findByValue(anyString())).thenReturn(EntityGenerator.getKanjiEntity());
		var kanji = BusinessObjectGenerator.getKanji();
		// ASSERT
		assertThrows(InvalidInputException.class, () -> kanjiServiceImpl.addKanji(kanji, false));
	}
	
	@Test
	public void findByValueTest() {
		// PREPARE
		when(kanjiRepository.findByValue(anyString()))
			.thenReturn(EntityGenerator.getKanjiEntity());
		// EXECUTE
		var kanjiEntity = kanjiServiceImpl.findByValue("");
		// ASSERT
		assertEquals(EntityGenerator.getKanjiEntity(), kanjiEntity);
	}

	@Test
	public void findByIdTestOk() {
		// PREPARE
		when(kanjiRepository.findById(anyLong()))
				.thenReturn(Optional.of(EntityGenerator.getKanjiEntity()));
		// EXECUTE
		var kanji = kanjiServiceImpl.findById(1L, null);
		// ASSERT
		assertEquals(kanjiMapper.toBusinessObject(EntityGenerator.getKanjiEntity()), kanji);
	}

	@Test
	public void findByIdTestKo() {
		// PREPARE
		when(kanjiRepository.findById(anyLong())).thenReturn(Optional.empty());
		// EXECUTE
		// ASSERT
		assertThrows(ItemNotFoundException.class, () -> kanjiServiceImpl.findById(1L, null));
	}

	@Test
	public void autoFillKanjiReadigsTestOk1() {
		// PREPARE
		var kanVO = new KanjiVO();
		var kunYomi = List.of("kunYomi");
		var onYomi = List.of("onYomi");
		var translations = Map.of(Language.EN, List.of("transation"));
		kanVO.setKunReadings(kunYomi);
		kanVO.setOnReadings(onYomi);
		kanVO.setMeanings(translations.get(Language.EN));
		when(kanjiApiClient.searchKanjiReadings(anyString())).thenReturn(kanVO);
		var kanji = new Kanji();
		kanji.setValue("T");
		// EXECUTE
		kanjiServiceImpl.autoFillKanjiReadigs(kanji);
		// ASSERT
		assertEquals(kunYomi, kanji.getKunYomi());
		assertEquals(onYomi, kanji.getOnYomi());
		assertEquals(translations, kanji.getTranslations());
	}
	
	@Test
	public void autoFillKanjiReadingsTestOk2() {
		// PREPARE
		when(kanjiApiClient.searchKanjiReadings(anyString())).thenThrow(new RuntimeException("Exception"));
		var kanji = new Kanji();
		kanji.setValue("T");
		// EXECUTE
		kanjiServiceImpl.autoFillKanjiReadigs(kanji);
		// ASSERT
		assertNull(kanji.getKunYomi());
		assertNull(kanji.getOnYomi());
		assertNull(kanji.getTranslations());
		
	}

	@Test
	public void getKanjisTestOk1() {
		// PREPARE
		when(kanjiRepository.findAllByOrderByTimeStampDesc(any(Pageable.class)))
			.thenReturn(new PageImpl<KanjiEntity>(List.of(EntityGenerator.getKanjiEntity())));
		var pageable = Pageable.ofSize(1);
		// EXECUTE
		var kanjis = kanjiServiceImpl.getKanjis(null, null, pageable).getContent();
		var kanji = kanjis.getFirst();
		// ASSERT
		assertEquals(1, kanjis.size());
		assertEquals(kanjiMapper.toBusinessObject(EntityGenerator.getKanjiEntity()), kanji);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getKanjisTestOk2() {
		// PREPARE
		when(kanjiRepository.findAll(any(Specification.class), any(Pageable.class)))
			.thenReturn(new PageImpl<>(List.of(EntityGenerator.getKanjiEntity())));
		var pageable = Pageable.ofSize(1);
		Kanji kanji = null;
		// EXECUTE
		var kanjisSearch = kanjiServiceImpl.getKanjis("T", null, pageable).getContent();
		// ASSERT
		assertEquals(1, kanjisSearch.size());
		kanji = kanjisSearch.get(0);
		assertEquals(kanjiMapper.toBusinessObject(EntityGenerator.getKanjiEntity()), kanji);
	}

	@Test
	public void patchKanjiTestOk() {
		// PREPARE
		when(kanjiRepository.findById(anyLong())).thenReturn(Optional.of(EntityGenerator.getKanjiEntity()));
		var patchRequest = mock(JsonNode.class);
		when(patchHelper.mergePatch(any(Kanji.class), any(JsonNode.class), eq(Kanji.class)))
			.thenReturn(BusinessObjectGenerator.getKanji());
		when(kanjiRepository.save(any(KanjiEntity.class))).thenReturn(EntityGenerator.getKanjiEntity());
		// EXECUTE
		var kanji = kanjiServiceImpl.patchKanji(1L, patchRequest);
		// ASSERT
		assertEquals(BusinessObjectGenerator.getKanji(), kanji);
	}
	
	@Test
	public void patchKanjiTestKo1() {
		// PREPARE
		when(kanjiRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		var patchRequest = mock(JsonNode.class);
		// EXECUTE
		// ASSERT
		assertThrows(ItemNotFoundException.class, () -> kanjiServiceImpl.patchKanji(1L, patchRequest));
	}
	
	@Test
	public void patchKanjiTestKo2() {
		// PREPARE
		when(kanjiRepository.findById(anyLong())).thenReturn(Optional.of(EntityGenerator.getKanjiEntity()));
		var patchRequest = mock(JsonNode.class);
		var kanjiMerge = Kanji.builder().id(111L).build();
		when(patchHelper.mergePatch(any(Kanji.class), any(JsonNode.class), eq(Kanji.class)))
			.thenReturn(kanjiMerge);
		// EXECUTE
		// ASSERT
		assertThrows(InvalidInputException.class, () -> kanjiServiceImpl.patchKanji(1L, patchRequest));
	}
	
}
