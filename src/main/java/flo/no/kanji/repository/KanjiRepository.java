package flo.no.kanji.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import flo.no.kanji.entity.KanjiEntity;

@Repository
public interface KanjiRepository extends JpaRepository<KanjiEntity, Long>, JpaSpecificationExecutor<KanjiEntity> {

	public List<KanjiEntity> findByOnYomi(String onYomi);

	public List<KanjiEntity> findByKunYomi(String kunYomi);

	public List<KanjiEntity> findByKunYomiOrOnYomiOrTranslationsContaining(String kunYomi, String onYomi,
			String translations);

	public KanjiEntity findByValue(String kanjiValue);

	public Page<KanjiEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

}
