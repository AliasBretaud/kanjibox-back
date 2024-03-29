package flo.no.kanji.business.service.impl;

import com.moji4j.MojiConverter;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.WordService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.integration.specification.WordSpecification;
import flo.no.kanji.util.CharacterUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Word business service implementation
 * @see WordService
 * @author Florian
 */
@Service
@Validated
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
	@Autowired
	private MojiConverter converter;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Word addWord(@Valid Word word) {

		// The word can't be already present in DB in order to be added
		checkWordAlreadyPresent(word);
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
	
	private void checkWordAlreadyPresent(final Word word) {
		Optional.ofNullable(wordRepository.findByValue(word.getValue())).ifPresent(k -> {
			throw new InvalidInputException(
					String.format("Word with value '%s' already exists in database", k.getValue()));
		});
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
	public Page<Word> getWords(String search, Language language, Pageable pageable) {

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
		
		var spec = WordSpecification.searchWord(search, this.converter);
		return wordRepository.findAll(spec, pageable).map(wordMapper::toBusinessObject);
	}
	
}
