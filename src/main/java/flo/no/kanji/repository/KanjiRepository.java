package flo.no.kanji.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import flo.no.kanji.entity.KanjiEntity;

@Repository
public interface KanjiRepository extends JpaRepository<KanjiEntity, Long> {

	public List<KanjiEntity> findByOnYomi(String onYomi);

	public List<KanjiEntity> findByKunYomi(String kunYomi);

	public List<KanjiEntity> findByKunYomiOrOnYomi(String kunYomi, String onYomi);

	public KanjiEntity findByValue(String kanjiValue);

	public List<KanjiEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

}
