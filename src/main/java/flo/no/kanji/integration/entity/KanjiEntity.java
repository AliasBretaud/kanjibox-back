package flo.no.kanji.integration.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "kanji")
@Setter
@Getter
public class KanjiEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ElementCollection
	@CollectionTable(name = "kanji_kun_yomi", joinColumns = @JoinColumn(name = "kanji_id"))
	@Column(name = "kun_yomi")
	private List<String> kunYomi;

	@ElementCollection
	@CollectionTable(name = "kanji_on_yomi", joinColumns = @JoinColumn(name = "kanji_id"))
	@Column(name = "on_yomi")
	private List<String> onYomi;

	@ElementCollection
	@CollectionTable(name = "kanji_translation", joinColumns = @JoinColumn(name = "kanji_id"))
	@Column(name = "translation")
	private List<String> translations;

	private LocalDateTime timeStamp;

	private String value;

	@PrePersist
	@PreUpdate
	private void setUp() {
		this.timeStamp = LocalDateTime.now();
	}
}
