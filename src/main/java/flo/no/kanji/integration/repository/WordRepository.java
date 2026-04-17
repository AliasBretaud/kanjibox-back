package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.WordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for WORD table
 *
 * @author Florian
 */
@Repository
public interface WordRepository extends JpaRepository<WordEntity, Long>, JpaSpecificationExecutor<WordEntity> {

    /**
     * Paginated list — relies on @BatchSize on entity collections rather than EntityGraph
     * to avoid the Hibernate in-memory pagination warning (HHH90003004).
     */
    Page<WordEntity> findAllByUserSubOrderByTimeStampDesc(String sub, Pageable pageable);

    Optional<WordEntity> findByValueAndUserSub(String value, String sub);

    /**
     * Single-entity lookup — lazy collections are loaded via @BatchSize within the @Transactional context.
     */
    Optional<WordEntity> findByIdAndUserSub(Long id, String sub);
}
