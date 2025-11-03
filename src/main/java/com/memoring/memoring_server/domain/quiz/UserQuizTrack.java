package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name="user_quiz_tracks",
        uniqueConstraints=@UniqueConstraint(name="uk_user_program",
                columnNames={"user_id","program_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserQuizTrack extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private QuizProgram program;

    @Column(nullable=false)
    private LocalDate startDate;

    @Column(nullable=false)
    private Integer currentIndex = 1;

    private LocalDate lastServedDate;

    @Column(nullable=false)
    private Boolean isCompleted = false;
}
