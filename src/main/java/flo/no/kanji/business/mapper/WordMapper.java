package flo.no.kanji.business.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.integration.entity.WordEntity;

/**
 * Word object bidirectional mapper between Model objects and Entities
 * 
 * @author Florian
 *
 */
@Service
public class WordMapper {

	@Autowired
	private KanjiMapper kanjiMapper;

	/**
	 * Transforms a Word entity to business object
	 * 
	 * @param wordEntity
	 * 			Input entity
	 * @return
	 * 			Transformed business word object
	 */
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
	
	/**
	 * Transforms a Word business object to entity (before performing save in database)
	 * 
	 * @param word
	 * 			Word business object
	 * @return
	 * 			Word entity converted object
	 */
	public WordEntity toEntity(Word word) {
		WordEntity wordEntity = new WordEntity();
		wordEntity.setId(word.getId());
		wordEntity.setValue(word.getValue());
		wordEntity.setFuriganaValue(word.getFuriganaValue());
		wordEntity.setTranslation(word.getTranslation());

		return wordEntity;
	}
}
