package flo.no.kanji.integration.specification;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.entity.WordEntity_;
import flo.no.kanji.util.CharacterUtils;
import org.springframework.data.jpa.domain.Specification;

/**
 * Word object JPA Specification utils class
 * @author Florian
 */
public class WordSpecification {
	
	public static Specification<WordEntity> searchWord(final String search,
													   final MojiConverter converter) {
		return (root, query, builder) -> {
			var characterTypSearch = CharacterUtils.getCharacterType(search);
			if (characterTypSearch == null) {
				throw new InvalidInputException("Invalid search value format");
			}
			var predicate = switch (characterTypSearch) {
				// Kana value (search based on furigana reading)
				case HIRAGANA, KATAKANA ->
					builder.equal(root.get(WordEntity_.furiganaValue), convertKanaToFurigana(search, converter));
					// Kanji value
				case KANJI, KANJI_WITH_OKURIGANA ->
					builder.like(root.get(WordEntity_.value), "%" + search + "%");
					// Romaji / translation
				case ROMAJI -> {
					var valueSearch = converter.convertRomajiToHiragana(search);
					yield builder.or(
							builder.equal(root.get(WordEntity_.furiganaValue), valueSearch),
							builder.like(builder.upper(root.get(WordEntity_.translation)),
									"%" + search.toUpperCase() + "%"));
				}
			};
			query.orderBy(builder.desc(root.get(WordEntity_.timeStamp)));
			
			return predicate;
		};
	}
	
	/**
	 * Converts a Kana value (hiragana or katakana) to a standard plain hiragana value
	 * 
	 * @param value
	 * 			Kana string input
	 * @return
	 * 			Hiragana converted value
	 */
	private static String convertKanaToFurigana(final String value, final MojiConverter converter) {
		// Since MojiConverter library doesn't provide a katakana to hiragana converting method we first convert
		// to romaji
		return CharacterUtils.isHiragana(value) ? value :
			converter.convertRomajiToHiragana(converter.convertKanaToRomaji(value));
	}
}
