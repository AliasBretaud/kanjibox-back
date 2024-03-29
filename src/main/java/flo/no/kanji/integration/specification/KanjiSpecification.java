package flo.no.kanji.integration.specification;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.KanjiEntity_;
import flo.no.kanji.integration.entity.TranslationEntity_;
import flo.no.kanji.util.CharacterUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Kanji object JPA Specification utils class
 * @author Florian
 */
public class KanjiSpecification {

	private static Predicate languageFilter(String language, Root<KanjiEntity> root, CriteriaBuilder cb) {
		var tr = root.join(KanjiEntity_.translations, JoinType.INNER);
		return cb.equal(tr.get(TranslationEntity_.language), language);
	}

	public static Specification<KanjiEntity> findById(final Long id, final String language) {
		return  (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get(KanjiEntity_.id), id));
			if (language != null) {
				predicates.add(languageFilter(language, root, criteriaBuilder));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
	
	/**
	 * Kanji search specification
	 * @param search
	 * 			Input character(s) search
	 * @return
	 * 			Builded search criteria specification
	 */
	public static Specification<KanjiEntity> searchKanji(final String search,
														 final String language,
														 final MojiConverter converter) {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			var characterTypeSearch = CharacterUtils.getCharacterType(search);
			if (characterTypeSearch == null) {
				throw new InvalidInputException("Invalid search value format");
			}
			var translatesJoin = root.join(KanjiEntity_.translations, JoinType.LEFT);
			if (language != null) {
				predicates.add(languageFilter(language, root, builder));
			}
			var searchPredicate = switch (characterTypeSearch) {
				// If search is hiragana only then find in kun yomi readings
				case HIRAGANA -> builder.equal(root.join(KanjiEntity_.kunYomi), search);
				// If search is katakana only then find in on yomi readings
				case KATAKANA -> builder.equal(root.join(KanjiEntity_.onYomi), search);
				// If search is a kanji then find by its value
				case KANJI, KANJI_WITH_OKURIGANA -> builder.equal(root.get(KanjiEntity_.value), search);
				// Romaji
				case ROMAJI -> {
					query.distinct(true);
					// Converting search in hiragana and katakana
					var kunYomi = converter.convertRomajiToHiragana(search);
					var onYomi = converter.convertRomajiToKatakana(search);

					// Joining tables kun/on yomi and translation for searching query
					var kunYomiJoin = root.join(KanjiEntity_.kunYomi, JoinType.LEFT);
					var onYomiJoin = root.join(KanjiEntity_.onYomi, JoinType.LEFT);
					var translatesText = translatesJoin.get(TranslationEntity_.translation);

					// Query building
					yield builder.or(builder.equal(kunYomiJoin, kunYomi), builder.equal(onYomiJoin, onYomi),
							builder.like(builder.upper(translatesText), "%" + search.toUpperCase() + "%"));
				}
			};

			// Order results by insertion time
			query.orderBy(builder.desc(root.get(KanjiEntity_.timeStamp)));
			predicates.add(searchPredicate);

			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
