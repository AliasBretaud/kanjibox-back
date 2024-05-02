package flo.no.kanji.unit.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.UserService;
import flo.no.kanji.business.service.impl.KanjiServiceImpl;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.TranslationEntity;
import flo.no.kanji.integration.entity.UserEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.mock.EntityGenerator;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.unit.business.mock.BusinessObjectGenerator;
import flo.no.kanji.unit.util.SecurityMockUtils;
import flo.no.kanji.util.PatchHelper;
import io.github.aliasbretaud.mojibox.data.KanjiEntry;
import io.github.aliasbretaud.mojibox.dictionary.KanjiDictionary;
import io.github.aliasbretaud.mojibox.enums.MeaningLanguage;
import io.github.aliasbretaud.mojibox.enums.ReadingType;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"kanji.translation.auto.enable=true"})
public class KanjiServiceTest {

    @Spy
    private final KanjiMapper kanjiMapper = Mockito.spy(KanjiMapper.class);

    @Mock
    private KanjiRepository kanjiRepository;

    @Mock
    private WordRepository wordRepository;

    @Mock
    private KanjiDictionary kanjiDictionary;

    @Mock
    private PatchHelper patchHelper;

    @Spy
    private MojiConverter mojiConverter;

    @Mock
    private UserService userService;

    @InjectMocks
    @Spy
    private KanjiServiceImpl kanjiServiceImpl;

    @BeforeEach
    public void setUp() {
        SecurityMockUtils.mockAuthentication();
    }

    /**
     * Nominal scenario, add kanji with all values filled
     */
    @Test
    public void addKanjiTestOk() {
        // PREPARE
        var kanji = BusinessObjectGenerator.getKanji();
        //EXECUTE
        var created = kanjiServiceImpl.addKanji(kanji, false, true);
        // ASSERT
        assertEquals(kanji, created);
    }

    /**
     * Test adding a kanji with auto-detection of readings/translations enabled
     */
    @Test
    public void addKanjiWithAutoDetectTestOk() {
        // PREPARE
        var entry = new KanjiEntry();
        entry.setMeanings(Map.of(MeaningLanguage.EN, List.of("en translation")));
        entry.setReadings(Map.of(ReadingType.JA_KUN, List.of("くんよみ")));
        when(kanjiDictionary.searchKanji(anyString())).thenReturn(entry);
        var kanji = new Kanji("白");
        //EXECUTE
        var created = kanjiServiceImpl.addKanji(kanji, true, true);
        // ASSERT
        assertEquals("白", created.getValue());
        assertEquals(List.of("くんよみ"), created.getKunYomi());
        assertEquals(Map.of(Language.EN, List.of("en translation")), created.getTranslations());
    }

    /**
     * Can't add a kanji if its value is already present in DB
     */
    @Test
    public void addKanjiTestKo() {
        // PREPARE
        when(kanjiRepository.findByValueAndUserSub(anyString(), anyString())).thenReturn(EntityGenerator.getKanjiEntity());
        var kanji = BusinessObjectGenerator.getKanji();
        // ASSERT
        assertThrows(InvalidInputException.class, () -> kanjiServiceImpl.addKanji(kanji, false, true));
    }

    @Test
    public void findByIdTestOk() {
        // PREPARE
        var kanjiMock = EntityGenerator.getKanjiEntity();
        kanjiMock.setWords(List.of(WordEntity.builder().value("漢字").build()));
        when(kanjiRepository.findById(anyLong()))
                .thenReturn(Optional.of(kanjiMock));
        // EXECUTE
        var kanji = kanjiServiceImpl.findById(1L);
        // ASSERT
        assertEquals(kanjiMock.getValue(), kanji.getValue());
        assertEquals(kanjiMock.getOnYomi(), kanji.getOnYomi());
        assertEquals(kanjiMock.getKunYomi(), kanji.getKunYomi());
        assertEquals(kanjiMock.getTranslations()
                        .stream().map(TranslationEntity::getTranslation).toList(),
                kanji.getTranslations().get(Language.EN));
        assertEquals(List.of("漢字"), kanji.getUsages());
    }

    @Test
    public void findByIdTestKo() {
        // PREPARE
        when(kanjiRepository.findById(anyLong())).thenReturn(Optional.empty());
        // EXECUTE
        // ASSERT
        assertThrows(ItemNotFoundException.class, () -> kanjiServiceImpl.findById(1L));
    }

    @Test
    public void autoFillKanjiReadingsTestOk() {
        // PREPARE
        when(kanjiDictionary.searchKanji(anyString()))
                .thenReturn(BusinessObjectGenerator.getKanjiVO());
        var kanji = new Kanji();
        kanji.setValue("T");
        // EXECUTE
        kanjiServiceImpl.autoFillKanjiReadigs(kanji);
        // ASSERT
        assertEquals(kanji.getValue(), "T");
        assertEquals(kanji.getKunYomi(), List.of("ひと"));
        assertEquals(kanji.getOnYomi(), List.of("ジン"));
    }

    @Test
    public void getKanjisTestOk1() {
        // PREPARE
        when(kanjiRepository.findAllByUserSubOrderByTimeStampDesc(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(EntityGenerator.getKanjiEntity())));
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
        // EXECUTE
        var kanjisSearch = kanjiServiceImpl.getKanjis("T", null, pageable).getContent();
        // ASSERT
        assertEquals(1, kanjisSearch.size());
        var kanji = kanjisSearch.getFirst();
        assertEquals(kanjiMapper.toBusinessObject(EntityGenerator.getKanjiEntity()), kanji);
    }

    @Test
    public void patchKanjiTestOk() {
        // PREPARE
        when(kanjiRepository.findById(anyLong())).thenReturn(Optional.of(EntityGenerator.getKanjiEntity()));
        var patchRequest = mock(JsonNode.class);
        when(patchHelper.mergePatch(any(Kanji.class), any(JsonNode.class), eq(Kanji.class)))
                .thenReturn(BusinessObjectGenerator.getKanji());
        when(userService.getCurrentUser()).thenReturn(new UserEntity("sub"));
        when(kanjiRepository.save(any(KanjiEntity.class))).thenReturn(EntityGenerator.getKanjiEntity());
        // EXECUTE
        var kanji = kanjiServiceImpl.patchKanji(1L, patchRequest);
        // ASSERT
        assertEquals(BusinessObjectGenerator.getKanji(), kanji);
    }

    @Test
    public void patchKanjiTestKo1() {
        // PREPARE
        when(kanjiRepository.findById(anyLong())).thenReturn(Optional.empty());
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

    @Test
    public void buildTranslationsTest() {
        // PREPARE
        var entry = new KanjiEntry();
        entry.setMeanings(Map.of(MeaningLanguage.EN, List.of("white dict"),
                MeaningLanguage.FR, List.of("blanc dict")));
        when(kanjiDictionary.searchKanji(eq("白"))).thenReturn(entry);
        var kanji = Kanji.builder()
                .value("白")
                .translations(Map.of(Language.FR, List.of("blanc test")))
                .build();
        // EXECUTE
        var translations = kanjiServiceImpl.buildTranslations(kanji);
        // ASSERT
        assertEquals(
                Map.of(Language.FR, List.of("blanc test"),
                        Language.EN, List.of("white dict")), translations);
    }

}
