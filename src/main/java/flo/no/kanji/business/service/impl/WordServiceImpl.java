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

@Service
public class WordServiceImpl implements WordService {

	@Autowired
	private KanjiService kanjiService;
	
	@Autowired
	private WordRepository wordRepository;

	@Autowired
	private WordMapper wordMapper;

	@Autowired
	private KanjiMapper kanjiMapper;
	
	private MojiConverter converter = new MojiConverter();

	@Override
	public Word addWord(Word word) {

		if (CollectionUtils.isEmpty(word.getKanjis())) {
			word.setKanjis(this.buildWordKanjisList(word.getValue()));
		}

		List<KanjiEntity> kanjis = word.getKanjis().stream().map(k -> {
			var kanjiEntity = kanjiService.findByValue(k.getValue());

			if (kanjiEntity == null) {
				kanjiService.autoFillKanjiReadigs(k);
				kanjiEntity = kanjiMapper.toEntity(k);
			}

			return kanjiEntity;
		}).collect(Collectors.toList());

		var wordEntity = wordMapper.toEntity(word);
		wordEntity.setKanjis(kanjis);

		return wordMapper.toBusinessObject(wordRepository.save(wordEntity));
	}

	private List<Kanji> buildWordKanjisList(String wordValue) {
		return wordValue.chars().mapToObj(i -> String.valueOf((char) i)).filter(CharacterUtils::isKanji)
				.map(Kanji::new).collect(Collectors.toList());
	}

	@Override
	public Page<Word> getWords(String search, Pageable pageable) {

		return ObjectUtils.isEmpty(search)
				? wordRepository.findAllByOrderByTimeStampDesc(pageable)
						.map(wordMapper::toBusinessObject)
				: this.searchWord(search, pageable);
	}
	
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
	
	private String convertKanaToFurigana(final String value) {
		return CharacterUtils.isHiragana(value) ? value :
			converter.convertRomajiToHiragana(converter.convertKanaToRomaji(value));
	}

}
