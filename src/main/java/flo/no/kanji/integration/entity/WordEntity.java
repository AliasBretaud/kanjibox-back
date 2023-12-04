package flo.no.kanji.integration.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Word entity persistent database object
 * @author Florian
 */
@Entity
@Table(name = "word")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordEntity {

	/** Database technical identifier **/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Transcripted hiragana value of the word **/
	private String furiganaValue;

	/** Associated kanjis composing the word **/
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "word_kanji", joinColumns = { @JoinColumn(name = "word_id") }, inverseJoinColumns = {
			@JoinColumn(name = "kanji_id") })
	private List<KanjiEntity> kanjis;

	/**
	 * Word creation/update timestamp
	 */
	private LocalDateTime timeStamp;

	/** Word translation */
	private String translation;

	/**
	 * Word japanese value (literal kanjis and okuriganas)
	 */
	@Column(name = "`value`")
	private String value;

	/**
	 * Default method called before each persist or update operation
	 */
	@PrePersist
	@PreUpdate
	private void setUp() {
		// Before each creation or upadte, setting current timestamp
		this.timeStamp = LocalDateTime.now();
	}
}
