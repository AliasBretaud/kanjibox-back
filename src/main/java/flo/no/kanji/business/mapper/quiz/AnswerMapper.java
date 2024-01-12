package flo.no.kanji.business.mapper.quiz;

import flo.no.kanji.business.model.quiz.Answer;
import flo.no.kanji.integration.entity.quiz.QuestionAnswerEntity;
import org.springframework.stereotype.Service;

@Service
public class AnswerMapper {

    public Answer toBusinessObject(final QuestionAnswerEntity entity) {
        return Answer.builder()
                .answered(entity.getAnswered())
                .isCorrect(entity.getIsCorrect())
                .build();
    }
}
