package flo.no.kanji.integration.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import flo.no.kanji.business.model.Kanji;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:testapi.properties")
@AutoConfigureMockMvc
public class KanjiControllerTest {

	@Autowired
    private MockMvc mockMvc;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
    public void testGetKanjiOk() throws Exception {
        mockMvc.perform(get("/kanjis?search=話"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].translations[0]", is("tale")));
    }
	
	@Test
	public void testPostKanjiOk1() throws Exception {
		var kanji = new Kanji("風");
		mockMvc.perform(post("/kanjis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(kanji)))
        	.andExpect(status().isOk());
	}
	
	@Test
	public void testPostKanjiOk2() throws Exception {
		var kanji = Kanji.builder()
				.value("古")
				.kunYomi(List.of("ふる.い"))
				.onYomi(List.of("コ"))
				.translations(List.of("Test"))
				.build();
		mockMvc.perform(post("/kanjis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(kanji)))
        	.andExpect(status().isOk());
	}
	
	@Test
	public void testPostKanjiKo1() throws Exception {
		mockMvc.perform(post("/kanjis")
				.content("")
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest());
		
	}
	
	@Test
	public void testPostKanjiKo2() throws Exception {
		var body = "{}";
		mockMvc.perform(post("/kanjis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(body))
        	.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testPostKanjiKo3() throws Exception {
		var kanji = Kanji.builder()
				.value("高")
				.kunYomi(List.of("tst"))
				.onYomi(List.of("コウ"))
				.translations(List.of("Test"))
				.build();
		mockMvc.perform(post("/kanjis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(kanji)))
        	.andExpect(status().isBadRequest());
	}
	
	@Test
	public void testPostKanjiKo4() throws Exception {
		var kanji = Kanji.builder()
				.value("高")
				.kunYomi(List.of("たか.い"))
				.onYomi(List.of("tst"))
				.translations(List.of("Test"))
				.build();
		mockMvc.perform(post("/kanjis")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(kanji)))
        	.andExpect(status().isBadRequest());
	}
}
