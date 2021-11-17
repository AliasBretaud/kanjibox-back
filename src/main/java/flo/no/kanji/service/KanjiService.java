package flo.no.kanji.service;

import java.util.List;

import flo.no.kanji.model.Kanji;

public interface KanjiService {

	public List<Kanji> searchKanji(String search);

	public Kanji addKanji(Kanji kanji, boolean autodetectReadings);

	public Kanji findKanjiByValue(String kanjiValue);

	public Kanji autoFillKanjiReadigs(Kanji kanji);

	public List<Kanji> getKanjis(Integer limit);
}
