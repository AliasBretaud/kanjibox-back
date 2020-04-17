package flo.no.kanji.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import flo.no.kanji.entity.WordEntity;

@Repository
public interface WordRepository extends JpaRepository<WordEntity, Long> {

	public List<WordEntity> findByValueContaining(String value);

	public List<WordEntity> findByFuriganaValueContaining(String value);

	public List<WordEntity> findByTranslationContaining(String traduction);

	public List<WordEntity> findAllByOrderByTimeStampDesc();
}
