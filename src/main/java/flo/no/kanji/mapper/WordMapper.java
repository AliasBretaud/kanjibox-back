package flo.no.kanji.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import flo.no.kanji.entity.WordEntity;
import flo.no.kanji.model.Kanji;
import flo.no.kanji.model.Word;

@Service
public class WordMapper {

	@Autowired
	private KanjiMapper kanjiMapper;

	public Word toBusinessObject(WordEntity wordEntity) {
		Word word = new Word();
		word.setId(wordEntity.getId());
		word.setValue(wordEntity.getValue());
		word.setFuriganaValue(wordEntity.getFuriganaValue());
		word.setTranslation(wordEntity.getTranslation());

		List<Kanji> kanjis = wordEntity.getKanjis().stream().map(kanjiMapper::toBusinessObject)
				.collect(Collectors.toList());
		word.setKanjis(kanjis);

		return word;
	}

	public WordEntity toEntity(Word word) {
		WordEntity wordEntity = new WordEntity();
		wordEntity.setId(word.getId());
		wordEntity.setValue(word.getValue());
		wordEntity.setFuriganaValue(word.getFuriganaValue());
		wordEntity.setTranslation(word.getTranslation());

		return wordEntity;
	}
}
