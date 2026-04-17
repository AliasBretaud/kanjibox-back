package flo.no.kanji.integration.specification;

import flo.no.kanji.integration.entity.TranslationEntity_;
import flo.no.kanji.integration.entity.UserEntity_;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.entity.WordEntity_;
import flo.no.kanji.util.CharacterUtils;
import flo.no.kanji.util.SearchQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Word object JPA Specification utils class
 *
 * @author Florian
 */
public class WordSpecification {

    private WordSpecification() {
    }

    public static Specification<WordEntity> searchWord(final SearchQuery query, final String userSub) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            var userJoin = root.join(WordEntity_.user, JoinType.INNER);
            predicates.add(builder.equal(userJoin.get(UserEntity_.sub), userSub));

            var searchPredicate = switch (query.type()) {
                case HIRAGANA, KATAKANA -> builder.equal(
                        root.get(WordEntity_.furiganaValue),
                        CharacterUtils.convertKanaToFurigana(query.raw()));
                case KANJI, KANJI_WITH_OKURIGANA -> builder.like(
                        root.get(WordEntity_.value), "%" + query.raw() + "%");
                case ROMAJI -> {
                    var translations = root.join(WordEntity_.translations, JoinType.LEFT)
                            .get(TranslationEntity_.translation);
                    yield builder.or(
                            builder.equal(root.get(WordEntity_.furiganaValue), query.hiragana()),
                            builder.like(builder.upper(translations), "%" + query.raw().toUpperCase() + "%"));
                }
            };

            criteriaQuery.orderBy(builder.desc(root.get(WordEntity_.timeStamp)));
            predicates.add(searchPredicate);

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
