package flo.no.kanji.integration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Kanji entity persistent database object
 *
 * @author Florian
 */
@Entity
@Table(name = "kanji")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KanjiEntity {

    /** Database technical identifier **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Kanji japanese style readings
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "kanji_kun_yomi", joinColumns = @JoinColumn(name = "kanji_id"))
    @Column(name = "kun_yomi")
    private List<String> kunYomi;

    /**
     * Kanji chinese style readings
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "kanji_on_yomi", joinColumns = @JoinColumn(name = "kanji_id"))
    @Column(name = "on_yomi")
    private List<String> onYomi;

    /**
     * Kanji translations
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "kanji_translation", joinColumns = @JoinColumn(name = "kanji_id"))
    private List<TranslationEntity> translations;

    /**
     * Kanji creation/update timestamp
     */
    private LocalDateTime timeStamp;

    /**
     * Kanji japanese value
     */
    @Column(name = "`value`", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** Associated words **/
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "kanjis")
    private List<WordEntity> words;

    /**
     * Default method called before each persist or update operation
     */
    @PrePersist
    @PreUpdate
    private void setUp() {
        // Before each creation or update, setting current timestamp
        this.timeStamp = LocalDateTime.now();
    }
}
