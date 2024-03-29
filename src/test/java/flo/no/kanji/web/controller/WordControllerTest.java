package flo.no.kanji.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:testapi.properties")
@AutoConfigureMockMvc
public class WordControllerTest {

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
	
}
