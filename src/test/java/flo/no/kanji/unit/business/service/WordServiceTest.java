package flo.no.kanji.unit.business.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.moji4j.MojiConverter;

import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.impl.WordServiceImpl;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.repository.WordRepository;

@ExtendWith(MockitoExtension.class)
public class WordServiceTest {

	@Mock
	private KanjiService kanjiService;
	
	@Mock
	private WordRepository wordRepository;

	@Spy
	private WordMapper wordMapper;

	@Spy
	private KanjiMapper kanjiMapper;
	
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
		// EXECUTE
		wordServiceImpl.addWord(word);
		var wordCaptor = ArgumentCaptor.forClass(WordEntity.class);
		verify(wordRepository).save(wordCaptor.capture());
		var savedWord = wordCaptor.getValue();
		// ASSERT
		assertEquals(word.getValue(), savedWord.getValue());
		assertEquals(word.getFuriganaValue(), savedWord.getFuriganaValue());
		assertEquals(word.getTranslation(), savedWord.getTranslation());
		var kanjiList = savedWord.getKanjis();
		assertEquals(2, kanjiList.size());
		assertEquals(kan1, kanjiList.get(0));
		assertEquals(kan2, kanjiList.get(1));
	}
	
	@Test
	public void addWordTestOk2() {
		// PREPARE
		when(wordRepository.save(any(WordEntity.class))).thenReturn(new WordEntity());
		doReturn(new Word()).when(wordMapper).toBusinessObject(any(WordEntity.class));
		reset(kanjiService);
		var word = new Word();
		word.setValue("きれいな火山");
		// EXECUTE
		wordServiceImpl.addWord(word);
		// ASSERT
		verify(kanjiService, times(2)).autoFillKanjiReadigs(any(Kanji.class));
	}
	
	@Test
	public void getWordsTest() {
		
	}

}
