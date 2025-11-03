package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name="quiz_attempts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAttempt extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private DailyQuizInstance dailyQuiz;

    private Instant startedAt;
    private Instant finishedAt;
    private Integer score;

    @OneToMany(mappedBy="attempt", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<QuizAnswer> answers;
}
