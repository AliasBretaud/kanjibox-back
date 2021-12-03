package flo.no.kanji.business.service;

import java.util.List;

import flo.no.kanji.business.model.Word;

public interface WordService {

	public List<Word> getWords(Integer limit);

	public Word addWord(Word word);
}
