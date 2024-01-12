package flo.no.kanji.integration.entity.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quiz_quiz_question")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizQuestionEntity {

    /** Database technical identifier **/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizEntity quiz;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @OneToOne(mappedBy = "quizQuestion", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private QuestionAnswerEntity answer;
}
