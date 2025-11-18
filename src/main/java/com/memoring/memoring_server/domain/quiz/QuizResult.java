package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "quiz_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizset_id", nullable = false)
    private QuizSet quizSet;

    @Column(nullable = false)
    private LocalDate takenAt;

    @Column(columnDefinition = "json", nullable = false)
    private String answer;

    public static QuizResult create(User user, QuizSet quizSet, LocalDate takenAt, String answer) {
        QuizResult qr = new QuizResult();
        qr.user = user;
        qr.quizSet = quizSet;
        qr.takenAt = takenAt;
        qr.answer = answer;
        return qr;
    }
}
