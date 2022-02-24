package flo.no.kanji.business.mapper;

import org.springframework.stereotype.Service;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;

/**
 * Kanji object bidirectional mapper between Model objects and Entities
 * 
 * @author Florian
 *
 */
@Service
public class KanjiMapper {

	/**
	 * Transforms a Kanji entity to business object
	 * 
	 * @param kanjiEntity
	 * 			Input entity
	 * @return
	 * 			Transformed business kanji object
	 */
	public Kanji toBusinessObject(KanjiEntity kanjiEntity) {
		Kanji kanji = new Kanji();
		kanji.setId(kanjiEntity.getId());
		kanji.setKunYomi(kanjiEntity.getKunYomi());
		kanji.setOnYomi(kanjiEntity.getOnYomi());
		kanji.setTranslations(kanjiEntity.getTranslations());
		kanji.setValue(kanjiEntity.getValue());

		return kanji;
	};
	
	/**
	 * Transforms a Kanji business object to entity (before performing save in database)
	 * 
	 * @param kanji
	 * 			Kanji business object
	 * @return
	 * 			Kanji entity converted object
	 */
	public KanjiEntity toEntity(Kanji kanji) {
		KanjiEntity kanjiEntity = new KanjiEntity();
		kanjiEntity.setId(kanji.getId());
		kanjiEntity.setKunYomi(kanji.getKunYomi());
		kanjiEntity.setOnYomi(kanji.getOnYomi());
		kanjiEntity.setTranslations(kanji.getTranslations());
		kanjiEntity.setValue(kanji.getValue());

		return kanjiEntity;
	};
}
