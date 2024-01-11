package flo.no.kanji.integration.entity.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_sentence")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SentenceEntity {
    /**
     * Database technical identifier
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
}
