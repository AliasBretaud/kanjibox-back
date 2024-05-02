package flo.no.kanji.unit.business.service;

import com.moji4j.MojiConverter;
import com.moji4j.MojiDetector;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.TranslationService;
import flo.no.kanji.business.service.UserService;
import flo.no.kanji.business.service.impl.WordServiceImpl;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.UserEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.mock.EntityGenerator;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.unit.business.mock.BusinessObjectGenerator;
import flo.no.kanji.unit.util.SecurityMockUtils;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.ListAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WordServiceTest {

    @InjectMocks
    @Spy
    private final WordMapper wordMapper = Mockito.spy(WordMapper.class);
    @Spy
    private KanjiMapper kanjiMapper = Mockito.spy(KanjiMapper.class);
    @Mock
    private KanjiService kanjiService;

    @Mock
    private KanjiRepository kanjiRepository;

    @Mock
    private WordRepository wordRepository;

    @Spy
    private MojiConverter converter;

    @Spy
    private MojiDetector detector;

    @Mock
    private TranslationService translationService;

    @Mock
    private UserService userService;

    @InjectMocks
    private WordServiceImpl wordServiceImpl;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(wordServiceImpl, "enableAutoDefaultTranslation", true);
        SecurityMockUtils.mockAuthentication();
    }

    /**
     * Nominal scenario : add a word with all the vales fulfilled
     */
    @Test
    public void addWordTestOk1() {
        // PREPARE
        when(translationService.translateValue(anyString(), eq(Language.FR)))
                .thenReturn("fr translation");
        var kan1 = KanjiEntity.builder().value("火").build();
        var kan2 = KanjiEntity.builder().value("山").build();
        when(kanjiRepository.findByValueInAndUserSub(anyList(), anyString())).thenReturn(List.of(kan1, kan2));
        var word = Word.builder().value("きれいな火山")
                .translations(Map.of(Language.EN, List.of("beautiful volcano")))
                .build();
        // EXECUTE
        var savedWord = wordServiceImpl.addWord(word, true);
        // ASSERT
        assertEquals(word.getValue(), savedWord.getValue());
        assertEquals(word.getFuriganaValue(), savedWord.getFuriganaValue());
        assertEquals(Map.of(
                Language.EN, List.of("beautiful volcano"),
                Language.FR, List.of("fr translation")), word.getTranslations());
        var kanjiList = savedWord.getKanjis();
        assertEquals(2, kanjiList.size());
        assertTrue(kanjiList.contains(kanjiMapper.toBusinessObject(kan1)));
        assertTrue(kanjiList.contains(kanjiMapper.toBusinessObject(kan2)));
    }

    /**
     * Test adding a word with auto-detection enabled
     */
    @Test
    public void addWordTestOk2() {
        // PREPARE
        var word = new Word("きれいな火山");
        when(translationService.translateValue(anyString(), eq(Language.EN)))
                .thenReturn("en translation");
        when(translationService.translateValue(anyString(), eq(Language.FR)))
                .thenReturn("fr translation");
        // EXECUTE
        var res = wordServiceImpl.addWord(word, true);
        // ASSERT
        assertEquals("きれいな火山", res.getValue());
        assertEquals("きれいなかざん", res.getFuriganaValue());
        assertEquals(List.of("en translation"), res.getTranslations().get(Language.EN));
        assertEquals(List.of("fr translation"), res.getTranslations().get(Language.FR));
    }

    /**
     * Can't add a word if it's value is already present in database
     */
    @Test
    public void addWordKo() {
        // PREPARE
        var word = BusinessObjectGenerator.getWord();
        when(wordRepository.findByValueAndUserSub(eq(word.getValue()), anyString())).thenReturn(EntityGenerator.getWordEntity());
        // EXECUTE/ASSERT
        assertThrows(InvalidInputException.class, () -> wordServiceImpl.addWord(word, true));
    }

    @Test
    public void getWordsWithoutSearchTest() {
        // PREPARE
        var page = new PageImpl<>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAllByUserSubOrderByTimeStampDesc(anyString(), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords(null, null, null, mock(Pageable.class)).getContent().getFirst();
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getWordsWithKunSearchTest() {
        // PREPARE
        var page = new PageImpl<>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords("あ", null, null, mock(Pageable.class)).getContent().getFirst();
        var specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
        executeSpecification(specCaptor.getValue());
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getWordsWithOnSearchTest() {
        // PREPARE
        var page = new PageImpl<>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords("ア", null, null, mock(Pageable.class)).getContent().getFirst();
        var specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
        executeSpecification(specCaptor.getValue());
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getWordsWithKanjiSearchTest() {
        // PREPARE
        var page = new PageImpl<>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords("亜", null, null, mock(Pageable.class)).getContent().getFirst();
        var specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
        executeSpecification(specCaptor.getValue());
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getWordsWithKanjiOkuriganaSearchTest() {
        // PREPARE
        var page = new PageImpl<>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords("会う", null, null, mock(Pageable.class)).getContent().getFirst();
        var specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
        executeSpecification(specCaptor.getValue());
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getWordsWithRomajiSearchTest() {
        // PREPARE
        var page = new PageImpl<>(List.of(EntityGenerator.getWordEntity()));
        when(wordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords("a", null, null, mock(Pageable.class)).getContent().getFirst();
        var specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(wordRepository).findAll(specCaptor.capture(), any(Pageable.class));
        executeSpecification(specCaptor.getValue());
        // ASSERT
        assertEquals(wordMapper.toBusinessObject(EntityGenerator.getWordEntity()), word);
    }

    @Test
    public void testDefaultTranslation() {
        // PREPARE
        when(userService.getCurrentUser()).thenReturn(new UserEntity("sub"));
        when(translationService.translateValue(anyString(), any(Language.class)))
                .thenReturn("Translated value");
        when(kanjiRepository.findByValueInAndUserSub(anyList(), anyString())).thenReturn(List.of(EntityGenerator.getKanjiEntity()));
        when(wordRepository.save(any(WordEntity.class))).thenReturn(EntityGenerator.getWordEntity());
        var word = Word.builder()
                .value("火山")
                .furiganaValue("かざん")
                .translations(Map.of(Language.FR, List.of("Volcan")))
                .build();
        // EXECUTE
        wordServiceImpl.addWord(word, false);
        // ASSERT
        verify(translationService, times(1)).translateValue(anyString(), any(Language.class));
    }

    @Test
    public void testWordListsLimit() {
        // PREPARE
        var wordEntity = WordEntity.builder()
                .value("言葉")
                .translations(Stream.of("Tr 1", "Tr 2", "Tr 3").map(t -> {
                    var translation = new TranslationEntity();
                    translation.setLanguage(Language.EN);
                    translation.setTranslation(t);
                    return translation;
                }).toList())
                .build();
        var page = new PageImpl<>(List.of(wordEntity));
        when(wordRepository.findAllByUserSubOrderByTimeStampDesc(anyString(), any(Pageable.class)))
                .thenReturn(page);
        // EXECUTE
        var word = wordServiceImpl.getWords(null, null, 2, mock(Pageable.class)).getContent().getFirst();
        // ASSERT
        assertEquals(List.of("Tr 1", "Tr 2"), word.getTranslations().get(Language.EN));
    }

    @SuppressWarnings("unchecked")
    private void executeSpecification(Specification<?> spec) {
        var root = mock(Root.class);
        lenient().when(root.join(nullable(ListAttribute.class), nullable(JoinType.class)))
                .thenReturn(mock(ListJoin.class));
        lenient().when(root.join(nullable(SingularAttribute.class), any(JoinType.class))).thenReturn(mock(Join.class));
        spec.toPredicate(root, mock(CriteriaQuery.class), mock(CriteriaBuilder.class));
    }

}
