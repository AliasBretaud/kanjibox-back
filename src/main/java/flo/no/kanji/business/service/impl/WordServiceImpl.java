package flo.no.kanji.business.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.mapper.WordMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.model.Word;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.business.service.WordService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.WordEntity;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.repository.WordRepository;
import flo.no.kanji.util.CharacterUtils;

@Service
public class WordServiceImpl implements WordService {

	@Autowired
	private KanjiService kanjiService;

	@Autowired
	private KanjiRepository kanjiRepository;

	@Autowired
	private WordRepository wordReposiory;

	@Autowired
	private WordMapper wordMapper;

	@Autowired
	private KanjiMapper kanjiMapper;

	@Override
	public Word addWord(Word word) {

		if (CollectionUtils.isEmpty(word.getKanjis())) {
			word.setKanjis(this.buildWordKanjisList(word.getValue()));
		}

		List<KanjiEntity> kanjis = word.getKanjis().stream().map(k -> {
			var kanjiEntity = kanjiRepository.findByValue(k.getValue());

			if (kanjiEntity == null) {
				kanjiService.autoFillKanjiReadigs(k);
				kanjiEntity = kanjiMapper.toEntity(k);
			}

			return kanjiEntity;
		}).collect(Collectors.toList());

		var wordEntity = wordMapper.toEntity(word);
		wordEntity.setKanjis(kanjis);

		return wordMapper.toBusinessObject(wordReposiory.save(wordEntity));
	}

	private List<Kanji> buildWordKanjisList(String wordValue) {
		return wordValue.chars().mapToObj(i -> String.valueOf((char) i)).filter(CharacterUtils::isKanji)
				.map(Kanji::new).collect(Collectors.toList());
	}

	@Override
	public List<Word> getWords(Integer limit) {

		List<WordEntity> result = limit != null ? wordReposiory.findAllByOrderByTimeStampDesc(PageRequest.of(0, limit))
				: wordReposiory.findAllByOrderByTimeStampDesc();

		return result.stream().map(wordMapper::toBusinessObject).collect(Collectors.toList());
	}

}
