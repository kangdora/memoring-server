package com.memoring.memoring_server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quizzes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizset_id", nullable = false)
    private QuizSet quizSet;

    @Column(columnDefinition = "text", nullable = false)
    private String prompt;

    public static Quiz create(QuizSet quizSet, String content, String prompt) {
        Quiz quiz = new Quiz();
        quiz.quizSet = quizSet;
        quiz.content = content;
        quiz.prompt = prompt;
        return quiz;
    }
}
