package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.WordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for WORD table
 *
 * @author Florian
 */
@Repository
public interface WordRepository extends JpaRepository<WordEntity, Long>, JpaSpecificationExecutor<WordEntity> {

    /**
     * Find all words, ordered by creation/modification date
     *
     * @param pageable Spring pageable settings
     * @param sub      User sub identifier
     * @return Words page
     */
    Page<WordEntity> findAllByUserSubOrderByTimeStampDesc(String sub, Pageable pageable);

    /**
     * Find a word by its value
     *
     * @param value word value
     * @param sub   user sub
     * @return retrieved word
     */
    WordEntity findByValueAndUserSub(String value, String sub);

}
