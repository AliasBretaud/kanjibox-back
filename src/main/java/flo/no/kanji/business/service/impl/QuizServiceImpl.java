package flo.no.kanji.business.service.impl;

import flo.no.kanji.business.mapper.quiz.QuestionMapper;
import flo.no.kanji.business.model.quiz.Question;
import flo.no.kanji.business.service.QuizService;
import flo.no.kanji.integration.repository.QuizQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Page<Question> getQuizQuestions(Long quizId, Pageable pageable) {
        return quizQuestionRepository.findByQuizId(quizId, pageable).map(questionMapper::toBusnessObject);
    }
}
