package flo.no.kanji.service;

import javax.json.JsonMergePatch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import flo.no.kanji.model.Kanji;

public interface KanjiService {

	Kanji addKanji(Kanji kanji, boolean autodetectReadings);

	Kanji findKanjiByValue(String kanjiValue);

	void autoFillKanjiReadigs(Kanji kanji);

	Page<Kanji> getKanjis(String search, Pageable pageable);

	Kanji patchKanji(Long kanjiId, JsonMergePatch patchRequest);
}
