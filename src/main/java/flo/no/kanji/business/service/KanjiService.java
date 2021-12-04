package flo.no.kanji.business.service;

import javax.json.JsonMergePatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.integration.entity.KanjiEntity;

public interface KanjiService {

	Kanji addKanji(Kanji kanji, boolean autodetectReadings);

	Kanji findKanjiByValue(String kanjiValue);

	void autoFillKanjiReadigs(Kanji kanji);

	Page<Kanji> getKanjis(String search, Pageable pageable);

	Kanji patchKanji(Long kanjiId, JsonMergePatch patchRequest);

	KanjiEntity findByValue(String value);
}
