package com.memoring.memoring_server.domain.quiz;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_sets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuizSet{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public static QuizSet create() {
        return new QuizSet();
    }
}
