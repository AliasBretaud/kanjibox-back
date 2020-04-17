package flo.no.kanji.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import flo.no.kanji.entity.KanjiEntity;
import flo.no.kanji.entity.WordEntity;
import flo.no.kanji.mapper.KanjiMapper;
import flo.no.kanji.mapper.WordMapper;
import flo.no.kanji.model.Kanji;
import flo.no.kanji.model.Word;
import flo.no.kanji.repository.KanjiRepository;
import flo.no.kanji.repository.WordRepository;
import flo.no.kanji.service.KanjiService;
import flo.no.kanji.service.WordService;
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
			KanjiEntity kanjiEntity = kanjiRepository.findByValue(k.getValue());

			if (kanjiEntity == null) {
				kanjiEntity = kanjiMapper.toEntity(kanjiService.autoFillKanjiReadigs(k));
			}

			return kanjiEntity;
		}).collect(Collectors.toList());

		WordEntity wordEntity = wordMapper.toEntity(word);
		wordEntity.setKanjis(kanjis);

		return wordMapper.toBusinessObject(wordReposiory.save(wordEntity));
	}

	private List<Kanji> buildWordKanjisList(String wordValue) {
		return wordValue.chars().mapToObj(i -> String.valueOf((char) i)).filter(s -> CharacterUtils.isKanji(s))
				.map(s -> {
					Kanji kanji = new Kanji();
					kanji.setValue(s);

					return kanji;
				}).collect(Collectors.toList());
	}

	@Override
	public List<Word> getWords() {
		return wordReposiory.findAllByOrderByTimeStampDesc().stream().map(wordMapper::toBusinessObject)
				.collect(Collectors.toList());
	}

}
