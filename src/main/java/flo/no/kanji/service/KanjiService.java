package flo.no.kanji.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import flo.no.kanji.model.Kanji;

public interface KanjiService {

	List<Kanji> searchKanji(String search);

	Kanji addKanji(Kanji kanji, boolean autodetectReadings);

	Kanji findKanjiByValue(String kanjiValue);

	void autoFillKanjiReadigs(Kanji kanji);

	Page<Kanji> getKanjis(String search, Pageable pageable);
}
