package flo.no.kanji.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import flo.no.kanji.entity.KanjiEntity;

@Repository
public interface KanjiRepository extends JpaRepository<KanjiEntity, Long>, JpaSpecificationExecutor<KanjiEntity> {

	KanjiEntity findByValue(String kanjiValue);

	Page<KanjiEntity> findAllByOrderByTimeStampDesc(Pageable pageable);

}
