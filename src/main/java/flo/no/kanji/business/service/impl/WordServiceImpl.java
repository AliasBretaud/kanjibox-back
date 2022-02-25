package flo.no.kanji.business.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.moji4j.MojiConverter;

import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.WordService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.entity.WordEntity_;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.util.CharacterUtils;

/**
 * Word business service implementation
 * 
 * @see WordService
 * @author Florian
 *
 */
@Service
public class WordServiceImpl implements WordService {

	/** Kanji operations business service */
	@Autowired
	private KanjiService kanjiService;
	
	/** Words JPA repository */
	@Autowired
	private WordRepository wordRepository;

	/** Word business/entity object mapper **/
	@Autowired
	private WordMapper wordMapper;

	/** Kanji business/entity object mapper **/
	@Autowired
	private KanjiMapper kanjiMapper;
	
	/** Japanese alphabets converting service **/
	private MojiConverter converter = new MojiConverter();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Word addWord(Word word) {

		// Initializing kanjis composing the word
		if (CollectionUtils.isEmpty(word.getKanjis())) {
			word.setKanjis(this.buildWordKanjisList(word.getValue()));
		}

		List<KanjiEntity> kanjis = word.getKanjis().stream().map(k -> {
			var kanjiEntity = kanjiService.findByValue(k.getValue());

			// If a kanji composing the word is unknown from the database, calling external API for determining
			// its readings and translations
			if (kanjiEntity == null) {
				kanjiService.autoFillKanjiReadigs(k);
				kanjiEntity = kanjiMapper.toEntity(k);
			}

			return kanjiEntity;
		}).collect(Collectors.toList());

		var wordEntity = wordMapper.toEntity(word);
		wordEntity.setKanjis(kanjis);

		// Saving and return created word
		return wordMapper.toBusinessObject(wordRepository.save(wordEntity));
	}

	/**
	 * Builds a kanji list composing a word
	 * 
	 * @param wordValue
	 * 			Word japanese writing value
	 * @return
	 * 			List of kanji business objects composing the word
	 */
	private List<Kanji> buildWordKanjisList(String wordValue) {
		return wordValue.chars().mapToObj(i -> String.valueOf((char) i)).filter(CharacterUtils::isKanji)
				.map(Kanji::new).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<Word> getWords(String search, Pageable pageable) {

		return ObjectUtils.isEmpty(search)
				? wordRepository.findAllByOrderByTimeStampDesc(pageable)
						.map(wordMapper::toBusinessObject)
				: this.searchWord(search, pageable);
	}
	
	/**
	 * Perform a word search based on the japanese writing value 
	 * 
	 * @param search
	 * 			Word's japanese writing value
	 * @param pageable
	 * 			Spring pageable request properties
	 * @return
	 * 			Spring page of retrieved corresponding words
	 */
	private Page<Word> searchWord(String search, Pageable pageable) {
		
		Specification<WordEntity> spec = (root, query, builder) -> {
			var predicate = switch (CharacterUtils.getCharacterType(search)) {
				// Kana value (search based on furigana reading)
				case HIRAGANA, KATAKANA ->
					builder.equal(root.get(WordEntity_.furiganaValue), this.convertKanaToFurigana(search));
				// Kanji value
				case KANJI, KANJI_WITH_OKURIGANA ->
					builder.like(root.get(WordEntity_.value), "%" + search + "%");
				// Romaji / translation
				default -> {
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
		
		return wordRepository.findAll(spec, pageable).map(wordMapper::toBusinessObject);
	}
	
	/**
	 * Converts a Kana value (hiragana or katakana) to a standard plain hiragana value
	 * 
	 * @param value
	 * 			Kana string input
	 * @return
	 * 			Hiragana converted value
	 */
	private String convertKanaToFurigana(final String value) {
		// Since MojiConverter library doens't provide a katakana to hiragana converting method we first convert
		// to romaji
		return CharacterUtils.isHiragana(value) ? value :
			converter.convertRomajiToHiragana(converter.convertKanaToRomaji(value));
	}

}
