package flo.no.kanji.service;

import java.util.List;

import flo.no.kanji.model.Word;

public interface WordService {

	public List<Word> getWords(Integer limit);

	public Word addWord(Word word);
}
