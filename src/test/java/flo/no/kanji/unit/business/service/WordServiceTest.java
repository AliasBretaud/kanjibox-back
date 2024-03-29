package flo.no.kanji.unit.business.service;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.TranslationMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Translation;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.impl.WordServiceImpl;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.mock.EntityGenerator;
import flo.no.kanji.integration.repository.WordRepository;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WordServiceTest {

	@Spy
	private TranslationMapper translationMapper = new TranslationMapper();

	@InjectMocks
	@Spy
	private KanjiMapper kanjiMapper = Mockito.spy(KanjiMapper.class);

	@InjectMocks
	@Spy
	private WordMapper wordMapper = Mockito.spy(WordMapper.class);

	@Mock
	private KanjiService kanjiService;
	
	@Mock
	private WordRepository wordRepository;
	
	@Spy
	private MojiConverter converter;
	
	@InjectMocks
	private WordServiceImpl wordServiceImpl;
	
	@Test
	public void addWordTestOk1() {
		// PREPARE
		var kan1 = KanjiEntity.builder().value("火").build();
		var kan2 = KanjiEntity.builder().value("山").build();
		when(kanjiService.findByValue(kan1.getValue())).thenReturn(kan1);
		when(kanjiService.findByValue(kan2.getValue())).thenReturn(kan2);
		when(wordRepository.save(any(WordEntity.class))).thenReturn(new WordEntity());
		doReturn(new Word()).when(wordMapper).toBusinessObject(any(WordEntity.class));
		var word = new Word();
		word.setValue("きれいな火山");
		word.setTranslations(List.of(new Translation("beautiful volcano", "en")));
		// EXECUTE
		wordServiceImpl.addWord(word);
		var wordCaptor = ArgumentCaptor.forClass(WordEntity.class);
		verify(wordRepository).save(wordCaptor.capture());
		var savedWord = wordCaptor.getValue();
		// ASSERT
		assertEquals(word.getValue(), savedWord.getValue());
		assertEquals(word.getFuriganaValue(), savedWord.getFuriganaValue());
		assertEquals(
				word.getTranslations().stream().map(Translation::getText).toList(),
				savedWord.getTranslations().stream().map(TranslationEntity::getTranslation).toList());
		var kanjiList = savedWord.getKanjis();
		assertEquals(2, kanjiList.size());
		assertEquals(kan1, kanjiList.get(0));
		assertEquals(kan2, kanjiList.get(1));
	}
	
	@Test
	public void addWordTestOk3() {
		// PREPARE
		when(wordRepository.save(any(WordEntity.class))).thenReturn(new WordEntity());
		doReturn(new Word()).when(wordMapper).toBusinessObject(any(WordEntity.class));
		reset(kanjiService);
		var word = new Word();
		word.setValue("きれいな火山");
		word.setKanjis(List.of(
				Kanji.builder().value("火").translations(List.of(new Translation("fire", "en"))).build(),
				Kanji.builder().value("山").translations(List.of(new Translation("mountain", "en"))).build()));
		word.setTranslations(List.of(new Translation("beautiful volcano", "en")));
		// EXECUTE
		wordServiceImpl.addWord(word);
		// ASSERT
		verify(kanjiService, times(2)).autoFillKanjiReadigs(any(Kanji.class));
	}
	
	@Test
	public void getWordsWithoutSearchTest() {
		// PREPARE
		var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
		when(wordRepository.findAllByOrderByTimeStampDesc(any(Pageable.class)))
			.thenReturn(page);
		// EXECUTE
		var word = wordServiceImpl.getWords(null, mock(Pageable.class)).getContent().get(0);
		// ASSERT
		assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getWordsWithKunSearchTest() {
		// PREPARE
		var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
		when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
			.thenReturn(page);
		// EXECUTE
		var word = wordServiceImpl.getWords("あ", mock(Pageable.class)).getContent().get(0);
		var specCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
		executeSpecification(specCaptor.getValue(), false);
		// ASSERT
		assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getWordsWithOnSearchTest() {
		// PREPARE
		var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
		when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
			.thenReturn(page);
		// EXECUTE
		var word = wordServiceImpl.getWords("ア", mock(Pageable.class)).getContent().get(0);
		var specCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
		executeSpecification(specCaptor.getValue(), false);
		// ASSERT
		assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getWordsWithKanjiSearchTest() {
		// PREPARE
		var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
		when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
			.thenReturn(page);
		// EXECUTE
		var word = wordServiceImpl.getWords("亜", mock(Pageable.class)).getContent().get(0);
		var specCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
		executeSpecification(specCaptor.getValue(), false);
		// ASSERT
		assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getWordsWithKanjiOkuriganaSearchTest() {
		// PREPARE
		var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
		when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
			.thenReturn(page);
		// EXECUTE
		var word = wordServiceImpl.getWords("会う", mock(Pageable.class)).getContent().get(0);
		var specCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
		executeSpecification(specCaptor.getValue(), false);
		// ASSERT
		assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void getWordsWithRomajiSearchTest() {
		// PREPARE
		var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
		when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
			.thenReturn(page);
		// EXECUTE
		var word = wordServiceImpl.getWords("a", mock(Pageable.class)).getContent().get(0);
		var specCaptor = ArgumentCaptor.forClass(Specification.class);
		verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
		executeSpecification(specCaptor.getValue(), true);
		// ASSERT
		assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
	}
	
	@SuppressWarnings("unchecked")
	private void executeSpecification(Specification<?> spec, boolean mockJoins) {
		var root = mock(Root.class);
		if (mockJoins) {
			when(root.join(nullable(ListAttribute.class), nullable(JoinType.class))).thenReturn(mock(ListJoin.class));
		}
		spec.toPredicate(root, mock(CriteriaQuery.class), mock(CriteriaBuilder.class));
	}

}
