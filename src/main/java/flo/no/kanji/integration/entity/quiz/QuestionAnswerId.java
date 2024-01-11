package flo.no.kanji.integration.entity.quiz;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class QuestionAnswerId implements Serializable {

    private Long quizId;

    private Long questionId;
}
