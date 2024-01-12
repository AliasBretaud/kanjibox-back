package flo.no.kanji.business.service;

import flo.no.kanji.business.model.quiz.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuizService {

    Page<Question> getQuizQuestions(final Long quizId, Pageable pageable);
}
