package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="quiz_programs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizProgram extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(nullable=false)
    private Integer totalItems;

    @Column(nullable=false)
    private Boolean isActive = true;

    @Column(length=20)
    private String version; // 선택
}
