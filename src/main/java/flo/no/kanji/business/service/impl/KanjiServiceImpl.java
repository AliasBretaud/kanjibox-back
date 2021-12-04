package flo.no.kanji.business.service.impl;

import java.time.LocalDateTime;

import javax.json.JsonMergePatch;
import javax.persistence.criteria.JoinType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import com.moji4j.MojiConverter;

import flo.no.kanji.api.KanjiApiClient;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.entity.KanjiEntity_;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.util.CharacterUtils;
import flo.no.kanji.util.PatchHelper;

@Service
public class KanjiServiceImpl implements KanjiService {

	@Autowired
	private KanjiRepository kanjiRepository;

	@Autowired
	private KanjiMapper kanjiMapper;

	@Autowired
	private KanjiApiClient kanjiApiClient;
	
	@Autowired
	private PatchHelper patchHelper;

	private MojiConverter converter = new MojiConverter();

	@Override
	public Kanji addKanji(Kanji kanji, boolean autoDetectReadings) {

		if (autoDetectReadings) {
			autoFillKanjiReadigs(kanji);
		}

		return kanjiMapper.toBusinessObject(kanjiRepository.save(kanjiMapper.toEntity(kanji)));
	}

	@Override
	public Kanji findKanjiByValue(String kanjiValue) {
		var kanjiEntity = kanjiRepository.findByValue(kanjiValue);
		return kanjiEntity != null ? kanjiMapper.toBusinessObject(kanjiEntity) : null;
	}

	@Override
	public void autoFillKanjiReadigs(Kanji kanji) {

		var kanjiVo = kanjiApiClient.searchKanjiReadings(kanji.getValue());
		if (kanjiVo != null) {
			kanji.setKunYomi(kanjiVo.getKunReadings());
			kanji.setOnYomi(kanjiVo.getOnReadings());
			kanji.setTranslations(kanjiVo.getMeanings());
		}
	}

	@Override
	public Page<Kanji> getKanjis(String search, Pageable pageable) {

		return ObjectUtils.isEmpty(search)
				? kanjiRepository.findAllByOrderByTimeStampDesc(pageable)
						.map(kanjiMapper::toBusinessObject)
				: this.searchKanji(search, pageable);
	}

	/**
	 * Execute a search query on different readings, translations or kanji value
	 * 
	 * @param search
	 * 			The input search value
	 * @param pageable
	 * 			Pagination parameter
	 * @return
	 * 		The result of search
	 */
	private Page<Kanji> searchKanji(String search, Pageable pageable) {

		Specification<KanjiEntity> spec = (root, query, builder) -> {
			var predicate = switch (CharacterUtils.getCharacterType(search)) {
				// If search is hiragana only then find in kun yomi readings
				case HIRAGANA -> builder.equal(root.join(KanjiEntity_.kunYomi), search);
				// If search is katakana only then find in on yomi readings
				case KATAKANA -> builder.equal(root.join(KanjiEntity_.onYomi), search);
				// If search is a kanji then find by its value
				case KANJI -> builder.equal(root.get(KanjiEntity_.value), search);
				// Presumed romaji
				default -> {
					query.distinct(true);
					// Converting search in hiragana and katakana 
					var kunYomi = converter.convertRomajiToHiragana(search);
					var onYomi = converter.convertRomajiToKatakana(search);
				
					// Joining tables kun/on yomi and translation for searching query
					var kunYomiJoin = root.join(KanjiEntity_.kunYomi, JoinType.LEFT);
					var onYomiJoin = root.join(KanjiEntity_.onYomi, JoinType.LEFT);
					var translatesJoin = root.join(KanjiEntity_.translations, JoinType.LEFT);
				
					// Query building
					yield builder.or(
						builder.equal(kunYomiJoin, kunYomi),
						builder.equal(onYomiJoin, onYomi),
						builder.like(builder.upper(translatesJoin), "%" + search.toUpperCase() + "%"));
				}
			};
			
			// Order results by insertion time
			query.orderBy(builder.desc(root.get(KanjiEntity_.timeStamp)));
			return predicate;
		};

		// Execute query, mapping and return results
		return kanjiRepository.findAll(spec, pageable).map(kanjiMapper::toBusinessObject);
	}

	@Override
	public Kanji patchKanji(Long kanjiId, JsonMergePatch patchRequest) {
		
		var initialKanji = this.findKanji(kanjiId);
		var patchedKanji = patchHelper.mergePatch(patchRequest, initialKanji,
				Kanji.class);
		
		// Prevent ID update
		if (patchedKanji.getId() != kanjiId) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		
		var patchedKanjiEntity = kanjiMapper.toEntity(patchedKanji);
		patchedKanjiEntity.setTimeStamp(LocalDateTime.now());
		patchedKanjiEntity = kanjiRepository.save(patchedKanjiEntity);
		
		return kanjiMapper.toBusinessObject(patchedKanjiEntity);
	}
	
	private Kanji findKanji(Long kanjiId) {
		return kanjiMapper.toBusinessObject(kanjiRepository.findById(kanjiId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	@Override
	public KanjiEntity findByValue(String value) {
		return kanjiRepository.findByValue(value);
	}

}
