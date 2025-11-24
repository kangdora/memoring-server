package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    boolean existsByUserAndQuizSetAndTakenAt(User user, QuizSet quizSet, LocalDate takenAt);
}
