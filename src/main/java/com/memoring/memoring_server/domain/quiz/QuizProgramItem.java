package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="quiz_program_items",
        uniqueConstraints=@UniqueConstraint(name="uk_program_order",
                columnNames={"program_id","orderIndex"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizProgramItem extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private QuizProgram program;

    @Column(nullable=false)
    private Integer orderIndex; // 1..n

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private QuestionType type;

    @Column(nullable=false, length=500)
    private String questionText;

    @Column(length=2000)
    private String choicesJson;

    @Column(length=1000)
    private String answerKey;
}
