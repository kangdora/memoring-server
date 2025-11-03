package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.media.MediaFile;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="quiz_answers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuizAnswer extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private QuizAttempt attempt;

    @Column(length=2000)
    private String answerText;

    private Boolean isCorrect;

    @ManyToOne(fetch=FetchType.LAZY)
    private MediaFile answerAudio;

    @Column(length=4000)
    private String transcribedText;

    private Double sttConfidence;
}
