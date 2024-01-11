package flo.no.kanji.integration.entity.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizEntity {

    /** Database technical identifier **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDateTime creationDate;

    private LocalDateTime updatedDate;

    private String theme;

    private Integer nbQuestions;

    private Integer correctAnswersCount;

    private Integer incorrectAnswersCount;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "quiz_questions", joinColumns = { @JoinColumn(name = "quiz_id") }, inverseJoinColumns = {
            @JoinColumn(name = "question_id") })
    private List<QuestionEntity> questions;
}
