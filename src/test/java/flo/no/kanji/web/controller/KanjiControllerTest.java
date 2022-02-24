package flo.no.kanji.web.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class KanjiControllerTest {

	@Autowired
    private MockMvc mockMvc;
	
	@Test
    public void testGetEmployees() throws Exception {
        mockMvc.perform(get("/kanjis?search=話"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].translations[0]", is("tale")));
    }
}
