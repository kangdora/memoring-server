package com.memoring.memoring_server.domain.caregiver;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_invites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareInvite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    public static CareInvite create(String code, Long patientId, LocalDateTime createdAt, LocalDateTime expiredAt) {
        CareInvite careInvite = new CareInvite();
        careInvite.code = code;
        careInvite.patientId = patientId;
        careInvite.createdAt = createdAt;
        careInvite.expiredAt = expiredAt;
        return careInvite;
    }
}
