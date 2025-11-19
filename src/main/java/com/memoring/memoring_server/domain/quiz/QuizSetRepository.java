package com.memoring.memoring_server.domain.quiz;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizSetRepository extends JpaRepository<QuizSet, Long> {

    List<QuizSet> findAllByOrderByIdAsc();

    long countByIdLessThanEqual(Long quizSetId);
}
