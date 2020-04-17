package flo.no.kanji.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import flo.no.kanji.model.Word;
import flo.no.kanji.service.WordService;

@RestController
public class WordController {

	@Autowired
	private WordService wordService;

	@GetMapping("/words")
	public List<Word> getWords() {
		return wordService.getWords();
	}

	@PostMapping("/words")
	public Word addWord(@RequestBody Word word) {
		return wordService.addWord(word);
	}
}
