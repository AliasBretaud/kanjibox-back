package flo.no.kanji.integration.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "word")
@Setter
@Getter
public class WordEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String furiganaValue;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "word_kanji", joinColumns = { @JoinColumn(name = "word_id") }, inverseJoinColumns = {
			@JoinColumn(name = "kanji_id") })
	private List<KanjiEntity> kanjis;

	private LocalDateTime timeStamp;

	private String translation;

	private String value;

	@PrePersist
	@PreUpdate
	private void setUp() {
		this.timeStamp = LocalDateTime.now();
	}
}
