package flo.no.kanji.business.mapper;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;
import org.springframework.stereotype.Service;

/**
 * Kanji object bidirectional mapper between Model objects and Entities
 * @author Florian
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
		return Kanji.builder()
				.id(kanjiEntity.getId())
				.kunYomi(kanjiEntity.getKunYomi())
				.onYomi(kanjiEntity.getOnYomi())
				.translations(kanjiEntity.getTranslations())
				.value(kanjiEntity.getValue())
				.build();
	}
	
	/**
	 * Transforms a Kanji business object to entity (before performing save in database)
	 * 
	 * @param kanji
	 * 			Kanji business object
	 * @return
	 * 			Kanji entity converted object
	 */
	public KanjiEntity toEntity(Kanji kanji) {
		return KanjiEntity.builder()
				.id(kanji.getId())
				.kunYomi(kanji.getKunYomi())
				.onYomi(kanji.getOnYomi())
				.translations(kanji.getTranslations())
				.value(kanji.getValue())
				.build();
	}
}
