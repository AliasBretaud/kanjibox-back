package flo.no.kanji.integration.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import flo.no.kanji.integration.entity.WordEntity;

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
	 * @param value
	 * 			Input contained in word translation
	 * @return
	 * 			Retrieved word list
	 */
	List<WordEntity> findByTranslationContaining(String traduction);

	/**
	 * Find all words, ordered by creation/modification date
	 * @param pageable
	 * 			Spring pageable settings
	 * @return
	 * 			Words page
	 */
	Page<WordEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

}
