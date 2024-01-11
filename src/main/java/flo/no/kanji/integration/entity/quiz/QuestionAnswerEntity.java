package flo.no.kanji.integration.entity.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_question_answer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionAnswerEntity {

    @EmbeddedId
    private QuestionAnswerId id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @MapsId("quizId")
    private QuizEntity quiz;

    @ManyToOne
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    @MapsId("questionId")
    private QuestionEntity question;

    private String answered;

    private boolean isCorrect;
}
