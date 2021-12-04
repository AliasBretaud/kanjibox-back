package flo.no.kanji.business.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import flo.no.kanji.business.model.Word;

public interface WordService {

	public Page<Word> getWords(String search, Pageable pageable);

	public Word addWord(Word word);
}
