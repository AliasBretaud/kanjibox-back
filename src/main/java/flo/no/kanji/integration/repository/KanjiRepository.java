package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.KanjiEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA repository for KANJI table
 *
 * @author Florian
 */
@Repository
public interface KanjiRepository extends JpaRepository<KanjiEntity, Long>, JpaSpecificationExecutor<KanjiEntity> {

    Optional<KanjiEntity> findByValueAndUserSub(String kanjiValue, String sub);

    /**
     * Single-entity lookup — lazy collections are loaded via @BatchSize within the @Transactional context.
     */
    Optional<KanjiEntity> findByIdAndUserSub(Long id, String sub);

    List<KanjiEntity> findByValueInAndUserSub(List<String> kanjiValues, String sub);

    /**
     * Paginated list — relies on @BatchSize on entity collections rather than EntityGraph
     * to avoid the Hibernate in-memory pagination warning (HHH90003004).
     */
    Page<KanjiEntity> findAllByUserSubOrderByTimeStampDesc(String sub, Pageable pageable);
}
