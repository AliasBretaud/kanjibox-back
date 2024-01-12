package flo.no.kanji.web.controller;

import flo.no.kanji.business.model.quiz.Question;
import flo.no.kanji.business.service.QuizService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Quiz REST Controller
 * @author Florian
 */
@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @GetMapping(path = "/{quizId}/questions")
    public Page<Question> getQuizQuestions(
            @PathVariable("quizId") final Long quizId,
            @ParameterObject @PageableDefault(size = 10) final Pageable pageable) {
        return this.quizService.getQuizQuestions(quizId, pageable);
    }
}
