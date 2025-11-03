package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name="daily_quiz_instances",
        uniqueConstraints=@UniqueConstraint(name="uk_user_quiz_date",
                columnNames={"user_id","quizDate"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyQuizInstance extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private User user;

    @Column(nullable=false)
    private LocalDate quizDate;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private QuizProgram program;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private QuizProgramItem programItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private QuizState status;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private QuestionType type; // 스냅샷 type

    @Column(nullable=false, length=500)
    private String snapshotQuestionText;

    @Column(length=2000)
    private String snapshotChoicesJson;

    @Column(length=1000)
    private String snapshotAnswerKey;
}
