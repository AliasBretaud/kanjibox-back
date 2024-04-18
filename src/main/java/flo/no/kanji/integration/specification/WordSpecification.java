package flo.no.kanji.integration.specification;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.integration.entity.TranslationEntity_;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.entity.WordEntity_;
import flo.no.kanji.util.CharacterUtils;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

/**
 * Word object JPA Specification utils class
 *
 * @author Florian
 */
public class WordSpecification {

    private static final MojiConverter converter = new MojiConverter();

    public static Specification<WordEntity> searchWord(final String search) {
        return (root, query, builder) -> {
            var characterTypSearch = CharacterUtils.getCharacterType(search);
            if (characterTypSearch == null) {
                throw new InvalidInputException("Invalid search value format");
            }
            var predicate = switch (characterTypSearch) {
                // Kana value (search based on furigana reading)
                case HIRAGANA, KATAKANA -> builder.equal(root.get(WordEntity_.furiganaValue),
                        CharacterUtils.convertKanaToFurigana(search));
                // Kanji value
                case KANJI, KANJI_WITH_OKURIGANA -> builder.like(root.get(WordEntity_.value), "%" + search + "%");
                // Romaji / translation
                case ROMAJI -> {
                    var valueSearch = converter.convertRomajiToHiragana(search);
                    var translations = root.join(WordEntity_.translations, JoinType.LEFT)
                            .get(TranslationEntity_.translation);
                    yield builder.or(
                            builder.equal(root.get(WordEntity_.furiganaValue), valueSearch),
                            builder.like(builder.upper(translations),
                                    "%" + search.toUpperCase() + "%"));
                }
            };
            query.orderBy(builder.desc(root.get(WordEntity_.timeStamp)));

            return predicate;
        };
    }
}
