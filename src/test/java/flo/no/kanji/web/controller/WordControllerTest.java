package flo.no.kanji.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:testapi.properties")
@AutoConfigureMockMvc
public class WordControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetWordOk1() throws Exception {
        mockMvc.perform(get("/words?search=大阪"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordOk2() throws Exception {
        mockMvc.perform(get("/words?search=おおさか"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordOk3() throws Exception {
        mockMvc.perform(get("/words?search=osaka"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordOk4() throws Exception {
        mockMvc.perform(get("/words?search=oosaka"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value", is("大阪")))
                .andExpect(jsonPath("$.content[0].furiganaValue", is("おおさか")))
                .andExpect(jsonPath("$.content[0].kanjis[0].value", is("大")))
                .andExpect(jsonPath("$.content[0].kanjis[1].value", is("阪")));
    }

    @Test
    public void testGetWordKo() throws Exception {
        mockMvc.perform(get("/words?search=*ryjy78598+('-"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostWordOk() throws Exception {
        var word = Word.builder()
                .value("食前")
                .furiganaValue("しょくぜん")
                .translations(Map.of(Language.EN, List.of("Before meal")))
                .build();

        mockMvc.perform(post("/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word)))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostWordKoNoTranslation() throws Exception {
        var word = Word.builder()
                .value("太陽")
                .furiganaValue("たいよう")
                .build();

        mockMvc.perform(post("/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(word)))
                .andExpect(status().isBadRequest());
    }
}
