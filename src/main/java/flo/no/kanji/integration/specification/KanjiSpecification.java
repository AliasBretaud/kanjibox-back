package flo.no.kanji.integration.specification;

import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.moji4j.MojiConverter;

import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.KanjiEntity_;
import flo.no.kanji.util.CharacterUtils;

/**
 * Kanji object JPA Specification utils class
 * @author Florian
 */
public class KanjiSpecification {

	/** Japanese/Latin character converter bean **/
	private static MojiConverter converter = new MojiConverter();
	
	/**
	 * Kanji search specification
	 * @param search
	 * 			Input character(s) search
	 * @return
	 * 			Builded search criteria specification
	 */
	public static Specification<KanjiEntity> getSearchKanjiSpecification(final String search) {
		return (root, query, builder) -> {
			var predicate = switch (CharacterUtils.getCharacterType(search)) {
			// If search is hiragana only then find in kun yomi readings
			case HIRAGANA -> builder.equal(root.join(KanjiEntity_.kunYomi), search);
			// If search is katakana only then find in on yomi readings
			case KATAKANA -> builder.equal(root.join(KanjiEntity_.onYomi), search);
			// If search is a kanji then find by its value
			case KANJI -> builder.equal(root.get(KanjiEntity_.value), search);
			// Presumed romaji
			default -> {
				query.distinct(true);
				// Converting search in hiragana and katakana
				var kunYomi = converter.convertRomajiToHiragana(search);
				var onYomi = converter.convertRomajiToKatakana(search);

				// Joining tables kun/on yomi and translation for searching query
				var kunYomiJoin = root.join(KanjiEntity_.kunYomi, JoinType.LEFT);
				var onYomiJoin = root.join(KanjiEntity_.onYomi, JoinType.LEFT);
				var translatesJoin = root.join(KanjiEntity_.translations, JoinType.LEFT);

				// Query building
				yield builder.or(builder.equal(kunYomiJoin, kunYomi), builder.equal(onYomiJoin, onYomi),
						builder.like(builder.upper(translatesJoin), "%" + search.toUpperCase() + "%"));
			}
			};

			// Order results by insertion time
			query.orderBy(builder.desc(root.get(KanjiEntity_.timeStamp)));
			return predicate;
		};
	}
}
