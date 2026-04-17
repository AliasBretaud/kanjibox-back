package flo.no.kanji.integration.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Kanji entity persistent database object
 *
 * @author Florian
 */
@Entity
@Table(name = "kanji")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class KanjiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "kanji_kun_yomi", joinColumns = @JoinColumn(name = "kanji_id"))
    @Column(name = "kun_yomi")
    @BatchSize(size = 20)
    private List<String> kunYomi;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "kanji_on_yomi", joinColumns = @JoinColumn(name = "kanji_id"))
    @Column(name = "on_yomi")
    @BatchSize(size = 20)
    private List<String> onYomi;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "kanji_translation", joinColumns = @JoinColumn(name = "kanji_id"))
    @BatchSize(size = 20)
    private List<TranslationEntity> translations;

    private LocalDateTime timeStamp;

    @Column(name = "`value`", nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "kanjis")
    @BatchSize(size = 20)
    private List<WordEntity> words;

    @PrePersist
    @PreUpdate
    private void setUp() {
        this.timeStamp = LocalDateTime.now();
    }
}
