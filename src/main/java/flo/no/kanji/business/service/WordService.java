package flo.no.kanji.business.service;

import com.fasterxml.jackson.databind.JsonNode;
import flo.no.kanji.business.constants.Language;
import flo.no.kanji.business.model.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Word operations business service
 *
 * @author Florian
 */
public interface WordService {

    Page<Word> getWords(String search, Language language, Integer listLimit, Pageable pageable, String userSub);

    Word addWord(Word word, boolean preview, String userSub);

    void deleteWord(Long wordId, String userSub);

    Word patchWord(Long wordId, JsonNode patch, String userSub);
}
