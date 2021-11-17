package flo.no.kanji.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.moji4j.MojiConverter;

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

	private MojiConverter converter = new MojiConverter();

	@Override
	public List<Kanji> searchKanji(String search) {

		List<KanjiEntity> result;

		if (CharacterUtils.isHiragana(search)) {
			result = kanjiRepository.findByKunYomi(search);

		} else if (CharacterUtils.isKatakana(search)) {
			result = kanjiRepository.findByOnYomi(search);
		} else if (CharacterUtils.isKanji(search)) {
			result = List.of(kanjiRepository.findByValue(search));
		} else {
			var kunYomi = converter.convertRomajiToHiragana(search);
			var onYomi = converter.convertRomajiToKatakana(search);
			result = kanjiRepository.findByKunYomiOrOnYomi(kunYomi, onYomi);
		}

		return result.stream().map(kanjiMapper::toBusinessObject).collect(Collectors.toList());
	}

	@Override
	public Kanji addKanji(Kanji kanji, boolean autoDetectReadings) {
		var kan = autoDetectReadings ? autoFillKanjiReadigs(kanji) : kanji;

		return kanjiMapper.toBusinessObject(kanjiRepository.save(kanjiMapper.toEntity(kan)));
	}

	@Override
	public Kanji findKanjiByValue(String kanjiValue) {
		var kanjiEntity = kanjiRepository.findByValue(kanjiValue);
		return kanjiEntity != null ? kanjiMapper.toBusinessObject(kanjiEntity) : null;
	}

	@Override
	public Kanji autoFillKanjiReadigs(Kanji kanji) {
		var kan = new Kanji();
		kan.setId(kanji.getId());
		kan.setValue(kanji.getValue());

		var kanjiVo = kanjiApiClient.searchKanjiReadings(kanji.getValue());
		if (kanjiVo != null) {
			kan.setKunYomi(kanjiVo.getKun_readings());
			kan.setOnYomi(kanjiVo.getOn_readings());
			kan.setTranslations(kanjiVo.getMeanings());
		}

		return kan;
	}

	@Override
	public List<Kanji> getKanjis(Integer limit) {
		List<KanjiEntity> list = limit == null ? kanjiRepository.findAll()
				: kanjiRepository.findAllByOrderByTimeStampDesc(PageRequest.of(0, limit));

		return list.stream().map(kanjiMapper::toBusinessObject).collect(Collectors.toList());
	}

}
