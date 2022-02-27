package flo.no.kanji.business.mapper;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		var kanjis = wordEntity.getKanjis().stream().map(kanjiMapper::toBusinessObject)
				.collect(Collectors.toList());
		return Word.builder()
				.id(wordEntity.getId())
				.value(wordEntity.getValue())
				.furiganaValue(wordEntity.getFuriganaValue())
				.translation(wordEntity.getTranslation())
				.kanjis(kanjis)
				.build();
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
		return WordEntity.builder()
				.id(word.getId())
				.value(word.getValue())
				.furiganaValue(word.getFuriganaValue())
				.translation(word.getTranslation())
				.build();
	}
}
