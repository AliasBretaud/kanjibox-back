package flo.no.kanji.integration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Word entity persistent database object
 *
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

    /** Transcript hiragana value of the word **/
    private String furiganaValue;

    /** Associated kanjis composing the word **/
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "word_kanji", joinColumns = {@JoinColumn(name = "word_id")}, inverseJoinColumns = {
            @JoinColumn(name = "kanji_id")})
    private List<KanjiEntity> kanjis;

    /**
     * Word creation/update timestamp
     */
    private LocalDateTime timeStamp;

    /**
     * Word translations
     */
    @ElementCollection
    @CollectionTable(name = "word_translation", joinColumns = @JoinColumn(name = "word_id"))
    private List<TranslationEntity> translations;

    /**
     * Word japanese value (literal kanjis and okuriganas)
     */
    @Column(name = "`value`")
    private String value;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

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
