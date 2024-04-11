package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.WordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for WORD table
 * @author Florian
 */
@Repository
public interface WordRepository extends JpaRepository<WordEntity, Long>, JpaSpecificationExecutor<WordEntity> {

	/**
	 * Find a word by its value containing an input string
	 * @param value
	 * 			Input contained in word
	 * @return
	 * 			Retrieved word list
	 */
	List<WordEntity> findByValueContaining(String value);

	/**
	 * Find a word by its hiragana transcription containing an input string
	 * @param value
	 * 			Input contained in word transcription
	 * @return
	 * 			Retrieved word list
	 */
	List<WordEntity> findByFuriganaValueContaining(String value);

	/**
	 * Find a word by its translation containing an input string
	 * @param translation
	 * 			Input contained in word translation
	 * @return
	 * 			Retrieved word list
	 */
	List<WordEntity> findByTranslationsContaining(String translation);

	/**
	 * Find all words, ordered by creation/modification date
	 * @param pageable
	 * 			Spring pageable settings
	 * @return
	 * 			Words page
	 */
	Page<WordEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

	/**
	 * Find a word by its value
	 * @param value 
	 * 			word value
	 * @return retrieved word
	 */
	WordEntity findByValue(String value);

}
