package flo.no.kanji.integration.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import flo.no.kanji.integration.entity.WordEntity;

@Repository
public interface WordRepository extends JpaRepository<WordEntity, Long> {

	List<WordEntity> findByValueContaining(String value);

	List<WordEntity> findByFuriganaValueContaining(String value);

	List<WordEntity> findByTranslationContaining(String traduction);

	List<WordEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

	List<WordEntity> findAllByOrderByTimeStampDesc();
}
