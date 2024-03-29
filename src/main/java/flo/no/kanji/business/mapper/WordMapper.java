package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Word;
import flo.no.kanji.integration.entity.WordEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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

	@Autowired
	private TranslationMapper translationMapper;

	/**
	 * Transforms a Word entity to business object
	 * @param wordEntity
	 * 			Input entity
	 * @return
	 * 			Transformed business word object
	 */
	public Word toBusinessObject(WordEntity wordEntity) {
		var kanjis = wordEntity.getKanjis() != null ? wordEntity.getKanjis().stream().map(kanjiMapper::toBusinessObject)
				.collect(Collectors.toList()) : null;
		return Word.builder()
				.id(wordEntity.getId())
				.value(wordEntity.getValue())
				.furiganaValue(wordEntity.getFuriganaValue())
				.translations(wordEntity.getTranslations().stream()
						.map(translationMapper::toBusinessObject).toList())
				.kanjis(kanjis)
				.build();
	}
	
	/**
	 * Transforms a Word business object to entity (before performing save in database)
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
				.translations(word.getTranslations().stream()
						.map(translationMapper::toEntity).toList())
				.build();
	}
}
