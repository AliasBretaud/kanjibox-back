package flo.no.kanji.business.mapper.quiz;

import flo.no.kanji.business.model.quiz.Question;
import flo.no.kanji.integration.entity.quiz.QuizQuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionMapper {

    @Autowired
    private AnswerMapper answerMapper;

    public Question toBusnessObject(final QuizQuestionEntity entity) {
        var answer = entity.getAnswer();
        return Question.builder()
                .id(entity.getQuestion().getId())
                .text(entity.getQuestion().getSentence().getText())
                .kanjiPosition(entity.getQuestion().getWordPosition())
                .propositions(entity.getQuestion().getPropositions())
                .answer(answer != null ? answerMapper.toBusinessObject(answer) : null)
                .build();
    }
}
