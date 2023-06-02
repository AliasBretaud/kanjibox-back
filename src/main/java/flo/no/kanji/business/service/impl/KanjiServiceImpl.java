package flo.no.kanji.business.service.impl;

import java.time.LocalDateTime;

import javax.json.JsonMergePatch;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;

import com.moji4j.MojiConverter;

import flo.no.kanji.business.exception.InvalidInputException;
import flo.no.kanji.business.exception.ItemNotFoundException;
import flo.no.kanji.business.mapper.KanjiMapper;
import flo.no.kanji.business.model.Kanji;
import flo.no.kanji.business.service.KanjiService;
import flo.no.kanji.integration.entity.KanjiEntity;
import flo.no.kanji.integration.repository.KanjiRepository;
import flo.no.kanji.integration.specification.KanjiSpecification;
import flo.no.kanji.util.PatchHelper;
import flo.no.kanji.web.api.KanjiApiClient;
import lombok.extern.slf4j.Slf4j;

/**
 * Kanji business service implementation
 * @see KanjiService
 * @author Florian
 */
@Service
@Slf4j
@Validated
public class KanjiServiceImpl implements KanjiService {

	/** Kanji JPA repository **/
	@Autowired
	private KanjiRepository kanjiRepository;

	/** Kanji business/entity object mapper */
	@Autowired
	private KanjiMapper kanjiMapper;

	/** External kanji readings/translations API client */
	@Autowired
	private KanjiApiClient kanjiApiClient;
	
	/** Kanji updating fields class helper */
	@Autowired
	private PatchHelper patchHelper;
	
	/** Japanese alphabets converting service **/
	@Autowired
	private MojiConverter converter;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Kanji addKanji(@Valid Kanji kanji, boolean autoDetectReadings) {

		checkKanjiAlreadyPresent(kanji);
		if (autoDetectReadings) {
			autoFillKanjiReadigs(kanji);
		}

		return kanjiMapper.toBusinessObject(kanjiRepository.save(kanjiMapper.toEntity(kanji)));
	}
	
	private void checkKanjiAlreadyPresent(final Kanji kanji) {
		if (kanjiRepository.findByValue(kanji.getValue()) != null) {
			throw new InvalidInputException(
					String.format("Kanji with value '%s' already exists in database", k.getValue()));
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void autoFillKanjiReadigs(Kanji kanji) {

		try {
			var kanjiVo = kanjiApiClient.searchKanjiReadings(kanji.getValue());
			if (kanjiVo != null) {
				kanji.setKunYomi(kanjiVo.getKunReadings());
				kanji.setOnYomi(kanjiVo.getOnReadings());
				kanji.setTranslations(kanjiVo.getMeanings());
			}
		} catch (Exception ex) {
			log.error("Error occured while retrieving kanji informations from API", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

		var spec = KanjiSpecification.getSearchKanjiSpecification(search, this.converter);
		// Execute query, mapping and return results
		return kanjiRepository.findAll(spec, pageable).map(kanjiMapper::toBusinessObject);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Kanji patchKanji(Long kanjiId, JsonMergePatch patchRequest) {
		
		var initialKanji = this.findKanji(kanjiId);
		var patchedKanji = patchHelper.mergePatch(patchRequest, initialKanji,
				Kanji.class);
		
		// Prevent ID update
		if (patchedKanji.getId() != kanjiId) {
			throw new InvalidInputException("ID update is forbidden");
		}
		
		var patchedKanjiEntity = kanjiMapper.toEntity(patchedKanji);
		patchedKanjiEntity.setTimeStamp(LocalDateTime.now());
		patchedKanjiEntity = kanjiRepository.save(patchedKanjiEntity);
		
		return kanjiMapper.toBusinessObject(patchedKanjiEntity);
	}
	
	/**
	 * Find a single kanji by its technical ID
	 * 
	 * @param kanjiId
	 * 			Kanji database identifier
	 * @return
	 * 			Retrieved kanji business object
	 */
	private Kanji findKanji(Long kanjiId) {
		return kanjiMapper.toBusinessObject(kanjiRepository.findById(kanjiId)
				.orElseThrow(() -> new ItemNotFoundException("Kanji with ID " + kanjiId + " not found")));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KanjiEntity findByValue(String value) {
		return kanjiRepository.findByValue(value);
	}

}
