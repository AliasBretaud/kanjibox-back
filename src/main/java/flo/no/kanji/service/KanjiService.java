package flo.no.kanji.service;

import java.util.List;

import flo.no.kanji.model.Kanji;

public interface KanjiService {

	List<Kanji> searchKanji(String search);

	Kanji addKanji(Kanji kanji, boolean autodetectReadings);

	Kanji findKanjiByValue(String kanjiValue);

	void autoFillKanjiReadigs(Kanji kanji);

	List<Kanji> getKanjis(Integer limit);
}
