package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.TranslationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class WordControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @MockBean
    JwtDecoder jwtDecoder;
    @MockBean
    private TranslationService translationService;
    @Autowired
    private MockMvc mockMvc;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor mockUser() {
        return jwt().jwt(jwt -> jwt.claim("sub", "auth0|662dc5e995203229af749169"));
    }

    @BeforeEach
    public void setUp() throws Exception {
        try (var mocks = MockitoAnnotations.openMocks(this)) {
            log.debug("init mocks : {}", mocks.toString());
        }
    }

    @Test
    public void testGetWordOk1() throws Exception {
        mockMvc.perform(get("/words?search=大阪").with(mockUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordOk2() throws Exception {
        mockMvc.perform(get("/words?search=おおさか").with(mockUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordOk3() throws Exception {
        mockMvc.perform(get("/words?search=osaka").with(mockUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordOk4() throws Exception {
        mockMvc.perform(get("/words?search=oosaka").with(mockUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordKo() throws Exception {
        mockMvc.perform(get("/words?search=*ryjy78598+('-").with(mockUser()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostWordOk() throws Exception {
        when(translationService.translateValue(anyString(), any(Language.class))).thenReturn("auto translation");
        var word = Word.builder()
                .value("食前")
                .furiganaValue("しょくぜん")
                .translations(Map.of(Language.EN, List.of("Before meal")))
                .build();

        mockMvc.perform(post("/words")
                        .with(mockUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.value", is("食前")))
                .andExpect(jsonPath("$.furiganaValue", is("しょくぜん")))
                .andExpect(jsonPath("$.translations", hasEntry(is("en"), contains("Before meal"))))
                .andExpect(jsonPath("$.translations", hasEntry(is("fr"), contains("auto translation"))));

    }

    @Test
    public void testPostWordOkAutoDetect() throws Exception {
        when(translationService.translateValue(anyString(), any(Language.class))).thenReturn("auto translation");
        var word = Word.builder()
                .value("食器")
                .build();

        mockMvc.perform(post("/words")
                        .with(mockUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.value", is("食器")))
                .andExpect(jsonPath("$.furiganaValue", is("しょっき")))
                .andExpect(jsonPath("$.translations", hasEntry(is("en"), contains("auto translation"))))
                .andExpect(jsonPath("$.translations", hasEntry(is("fr"), contains("auto translation"))));
    }

    @Test
    public void deleteWordOk() throws Exception {
        mockMvc.perform(delete("/words/80")
                        .with(mockUser()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteWordKo() throws Exception {
        mockMvc.perform(delete("/words/1111")
                        .with(mockUser()))
                .andExpect(status().isNotFound());
    }
}
