package flo.no.kanji.integration.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Word entity persistent database object
 *
 * @author Florian
 */
@Entity
@Table(name = "word")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String furiganaValue;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "word_kanji",
            joinColumns = {@JoinColumn(name = "word_id")},
            inverseJoinColumns = {@JoinColumn(name = "kanji_id")})
    @BatchSize(size = 20)
    private List<KanjiEntity> kanjis;

    private LocalDateTime timeStamp;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "word_translation", joinColumns = @JoinColumn(name = "word_id"))
    @BatchSize(size = 20)
    private List<TranslationEntity> translations;

    @Column(name = "`value`")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @PrePersist
    @PreUpdate
    private void setUp() {
        this.timeStamp = LocalDateTime.now();
    }
}
