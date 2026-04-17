package flo.no.kanji.integration.specification;

import flo.no.kanji.business.constants.Language;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.KanjiEntity_;
import flo.no.kanji.integration.entity.TranslationEntity_;
import flo.no.kanji.integration.entity.UserEntity_;
import flo.no.kanji.util.SearchQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Kanji object JPA Specification utils class
 *
 * @author Florian
 */
public class KanjiSpecification {

    private KanjiSpecification() {
    }

    private static Predicate languageFilter(Language language, Root<KanjiEntity> root, CriteriaBuilder cb) {
        var tr = root.join(KanjiEntity_.translations, JoinType.INNER);
        return cb.equal(tr.get(TranslationEntity_.language), language);
    }

    public static Specification<KanjiEntity> searchKanji(final SearchQuery query,
                                                         final Language language,
                                                         final String userSub) {
        return (root, criteriaQuery, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            var userJoin = root.join(KanjiEntity_.user, JoinType.INNER);
            predicates.add(builder.equal(userJoin.get(UserEntity_.sub), userSub));

            var translatesJoin = root.join(KanjiEntity_.translations, JoinType.LEFT);
            if (language != null) {
                predicates.add(languageFilter(language, root, builder));
            }

            var searchPredicate = switch (query.type()) {
                case HIRAGANA -> builder.equal(root.join(KanjiEntity_.kunYomi), query.raw());
                case KATAKANA -> builder.equal(root.join(KanjiEntity_.onYomi), query.raw());
                case KANJI, KANJI_WITH_OKURIGANA -> builder.equal(root.get(KanjiEntity_.value), query.raw());
                case ROMAJI -> {
                    criteriaQuery.distinct(true);
                    var kunYomiJoin = root.join(KanjiEntity_.kunYomi, JoinType.LEFT);
                    var onYomiJoin = root.join(KanjiEntity_.onYomi, JoinType.LEFT);
                    var translatesText = translatesJoin.get(TranslationEntity_.translation);
                    yield builder.or(
                            builder.equal(kunYomiJoin, query.hiragana()),
                            builder.equal(onYomiJoin, query.katakana()),
                            builder.like(builder.upper(translatesText), "%" + query.raw().toUpperCase() + "%"));
                }
            };

            criteriaQuery.orderBy(builder.desc(root.get(KanjiEntity_.timeStamp)));
            predicates.add(searchPredicate);

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
