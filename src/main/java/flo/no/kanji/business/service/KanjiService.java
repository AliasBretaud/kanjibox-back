package flo.no.kanji.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Kanji;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Kanji operations business service
 *
 * @author Florian
 */
public interface KanjiService {

    Kanji addKanji(Kanji kanji, boolean autoDetect, boolean preview, String userSub);

    Map<Language, List<String>> buildTranslations(Kanji kanji);

    void autoFillKanjiReadigs(Kanji kanji);

    Page<Kanji> getKanjis(String search, Language language, Pageable pageable, String userSub);

    Kanji patchKanji(Long kanjiId, JsonNode patch, String userSub);

    Kanji findById(Long kanjiId, String userSub);

    List<Kanji> findByValues(List<String> values, String userSub);

    void deleteKanji(Long kanjiId, String userSub);
}
