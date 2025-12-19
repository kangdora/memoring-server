package com.memoring.memoring_server.domain.caregiver;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_relations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientId;

    @Column(nullable = false)
    private String caregiverId;

    @Column(nullable = false)
    private LocalDateTime connectedAt;

    public static CareRelation create(String patientId, String caregiverId, LocalDateTime connectedAt) {
        CareRelation careRelation = new CareRelation();
        careRelation.patientId = patientId;
        careRelation.caregiverId = caregiverId;
        careRelation.connectedAt = connectedAt;
        return careRelation;
    }
}
