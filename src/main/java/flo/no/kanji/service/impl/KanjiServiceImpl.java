package flo.no.kanji.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import flo.no.kanji.api.KanjiApiClient;
import flo.no.kanji.api.model.KanjiVO;
import flo.no.kanji.entity.KanjiEntity;
import flo.no.kanji.mapper.KanjiMapper;
import flo.no.kanji.model.Kanji;
import flo.no.kanji.repository.KanjiRepository;
import flo.no.kanji.service.KanjiService;
import flo.no.kanji.util.CharacterUtils;

@Service
public class KanjiServiceImpl implements KanjiService {

	@Autowired
	private KanjiRepository kanjiRepository;

	@Autowired
	private KanjiMapper kanjiMapper;

	@Autowired
	private KanjiApiClient kanjiApiClient;

	@Override
	public List<Kanji> searchKanji(String search) {

		List<KanjiEntity> result;

		if (CharacterUtils.isHiragana(search)) {
			result = kanjiRepository.findByKunYomi(search);

		} else if (CharacterUtils.isKatakana(search)) {
			result = kanjiRepository.findByOnYomi(search);

		} else {
			result = Arrays.asList(kanjiRepository.findByValue(search));
		}

		return result.stream().map(kanjiMapper::toBusinessObject).collect(Collectors.toList());
	}

	@Override
	public Kanji addKanji(Kanji kanji, boolean autoDetectReadings) {
		Kanji kan = autoDetectReadings ? autoFillKanjiReadigs(kanji) : kanji;

		return kanjiMapper.toBusinessObject(kanjiRepository.save(kanjiMapper.toEntity(kan)));
	}

	@Override
	public Kanji findKanjiByValue(String kanjiValue) {
		KanjiEntity kanjiEntity = kanjiRepository.findByValue(kanjiValue);
		return kanjiEntity != null ? kanjiMapper.toBusinessObject(kanjiEntity) : null;
	}

	@Override
	public Kanji autoFillKanjiReadigs(Kanji kanji) {
		Kanji kan = new Kanji();
		kan.setId(kanji.getId());
		kan.setValue(kanji.getValue());

		KanjiVO kanjiVo = kanjiApiClient.searchKanjiReadings(kanji.getValue());
		if (kanjiVo != null) {
			kan.setKunYomi(kanjiVo.getKun_readings());
			kan.setOnYomi(kanjiVo.getOn_readings());
			kan.setTranslations(kanjiVo.getMeanings());
		}

		return kan;
	}

	@Override
	public List<Kanji> getRecentsKanjis(int limit) {
		List<KanjiEntity> list = kanjiRepository.findAllByOrderByTimeStampDesc(PageRequest.of(0, limit));

		return list.stream().map(kanjiMapper::toBusinessObject).collect(Collectors.toList());
	}

}
