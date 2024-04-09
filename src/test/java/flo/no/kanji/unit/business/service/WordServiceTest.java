package flo.no.kanji.unit.business.service;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.TranslationService;
import flo.no.kanji.business.service.impl.WordServiceImpl;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.mock.EntityGenerator;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WordServiceTest {

    @Spy
    private KanjiMapper kanjiMapper = Mockito.spy(KanjiMapper.class);

    @InjectMocks
    @Spy
    private WordMapper wordMapper = Mockito.spy(WordMapper.class);

    @Mock
    private KanjiService kanjiService;

    @Mock
    private KanjiRepository kanjiRepository;

    @Mock
    private WordRepository wordRepository;

    @Spy
    private MojiConverter converter;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private WordServiceImpl wordServiceImpl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(wordServiceImpl, "enableAutoDefaultTranslation", true);
    }

    @Test
    public void addWordTestOk1() {
        // PREPARE
        var kan1 = KanjiEntity.builder().value("火").build();
        var kan2 = KanjiEntity.builder().value("山").build();
        when(kanjiRepository.findByValueIn(anyList())).thenReturn(List.of(kan1, kan2));
        when(wordRepository.save(any(WordEntity.class))).thenReturn(new WordEntity());
        doReturn(new Word()).when(wordMapper).toBusinessObject(any(WordEntity.class));
        var word = new Word();
        word.setValue("きれいな火山");
        word.setTranslations(Map.of(Language.EN, List.of("beautiful volcano")));
        // EXECUTE
        wordServiceImpl.addWord(word);
        var wordCaptor = ArgumentCaptor.forClass(WordEntity.class);
        verify(wordRepository).save(wordCaptor.capture());
        var savedWord = wordCaptor.getValue();
        // ASSERT
        assertEquals(word.getValue(), savedWord.getValue());
        assertEquals(word.getFuriganaValue(), savedWord.getFuriganaValue());
        assertEquals(
                word.getTranslations().get(Language.EN),
                savedWord.getTranslations().stream().map(TranslationEntity::getTranslation).toList());
        var kanjiList = savedWord.getKanjis();
        assertEquals(2, kanjiList.size());
        assertTrue(kanjiList.contains(kan1));
        assertTrue(kanjiList.contains(kan2));
    }

    @Test
    public void addWordTestOk2() {
        // PREPARE
        var wordEntity = EntityGenerator.getWordEntity();
        when(wordRepository.save(any(WordEntity.class))).thenReturn(wordEntity);
        var word = new Word();
        word.setValue("きれいな火山");
        word.setKanjis(List.of(
                Kanji.builder().value("火").translations(Map.of(Language.EN, List.of("fire"))).build(),
                Kanji.builder().value("山").translations(Map.of(Language.EN, List.of("mountain"))).build()));
        word.setTranslations(Map.of(Language.EN, List.of("Volcano")));
        // EXECUTE
        var res = wordServiceImpl.addWord(word);
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(wordEntity), res);
    }

    @Test
    public void getWordsWithoutSearchTest() {
        // PREPARE
        var page = new PageImpl<WordEntity>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAllByOrderByTimeStampDesc(any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords(null, null, mock(Pageable.class)).getContent().get(0);
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
        var word = wordServiceImpl.getWords("あ", null, mock(Pageable.class)).getContent().get(0);
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
        var word = wordServiceImpl.getWords("ア", null, mock(Pageable.class)).getContent().get(0);
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
        var word = wordServiceImpl.getWords("亜", null, mock(Pageable.class)).getContent().get(0);
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
        var word = wordServiceImpl.getWords("会う", null, mock(Pageable.class)).getContent().get(0);
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
        var word = wordServiceImpl.getWords("a", null, mock(Pageable.class)).getContent().get(0);
        var specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
        executeSpecification(specCaptor.getValue(), true);
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    public void testDefaultTranslation() {
        // PREPARE
        when(translationService.translateValue(anyString(), any(Language.class)))
                .thenReturn("Translated value");
        when(kanjiRepository.findByValueIn(anyList())).thenReturn(List.of(EntityGenerator.getKanjiEntity()));
        when(wordRepository.save(any(WordEntity.class))).thenReturn(EntityGenerator.getWordEntity());
        var word = Word.builder()
                .value("火山")
                .furiganaValue("かざん")
                .translations(Map.of(Language.FR, List.of("Volcan")))
                .build();
        // EXECUTE
        wordServiceImpl.addWord(word);
        // ASSERT
        verify(translationService, times(1)).translateValue(anyString(), any(Language.class));
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
