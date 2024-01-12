package flo.no.kanji.integration.entity.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "quiz_question")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionEntity {

    /**
     * Database technical identifier
     **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="sentence_id", nullable = false)
    private SentenceEntity sentence;

    private Integer wordPosition;

    private String reading;

    @ElementCollection
    @CollectionTable(name = "quiz_question_propositions", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "proposition")
    private List<String> propositions;

}