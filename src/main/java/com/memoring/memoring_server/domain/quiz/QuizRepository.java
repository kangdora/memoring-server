package com.memoring.memoring_server.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByQuizSetInOrderByQuizSetIdAscIdAsc(List<QuizSet> quizSets);

    List<Quiz> findAllByQuizSetOrderByIdAsc(QuizSet quizSet);
}
