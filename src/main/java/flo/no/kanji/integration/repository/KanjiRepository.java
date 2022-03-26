package flo.no.kanji.integration.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import flo.no.kanji.integration.entity.KanjiEntity;

/**
 * JPA repository for KANJI table
 * @author Florian
 */
@Repository
public interface KanjiRepository extends JpaRepository<KanjiEntity, Long>, JpaSpecificationExecutor<KanjiEntity> {

	/**
	 * Find a kanji by its japanese writing value
	 * @param kanjiValue
	 * 			japanese writing value
	 * @return
	 * 			Found kanji
	 */
	KanjiEntity findByValue(String kanjiValue);

	/**
	 * Find all kanjis, ordered by creation/modification date
	 * @param pageable
	 * 			Spring pageable settings
	 * @return
	 * 			Kanjis page
	 */
	Page<KanjiEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

}
