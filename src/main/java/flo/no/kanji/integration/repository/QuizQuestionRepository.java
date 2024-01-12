package flo.no.kanji.integration.repository;

import flo.no.kanji.integration.entity.quiz.QuizQuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuizQuestionRepository extends JpaRepository<QuizQuestionEntity, Long> {

    @Query(
    value = """
    select qq from QuizQuestionEntity qq
    join fetch qq.question q
    join fetch q.propositions props
    join fetch q.sentence sen
    left join fetch qq.answer ans
    join fetch qq.quiz quiz
    where quiz.id = :quizId
    """, countQuery = "select count(qq) from QuizQuestionEntity qq where qq.quiz.id = :quizId")
    Page<QuizQuestionEntity> findByQuizId(@Param("quizId") final Long quizId, Pageable pageable);
}
