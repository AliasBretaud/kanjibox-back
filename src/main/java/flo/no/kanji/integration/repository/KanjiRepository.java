package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.KanjiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA repository for KANJI table
 *
 * @author Florian
 */
@Repository
public interface KanjiRepository extends JpaRepository<KanjiEntity, Long>, JpaSpecificationExecutor<KanjiEntity> {

    /**
     * Find a kanji by its japanese writing value
     *
     * @param kanjiValue japanese writing value
     * @return Found kanji
     */
    KanjiEntity findByValue(String kanjiValue);

    /**
     * Find a list of kanji by japanese writing values
     *
     * @param kanjiValues japanese writing values
     * @return Found kanjis
     */
    List<KanjiEntity> findByValueIn(List<String> kanjiValues);

    /**
     * Find all kanjis, ordered by creation/modification date
     *
     * @param pageable Spring pageable settings
     * @return Kanjis page
     */
    Page<KanjiEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

}
