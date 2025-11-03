package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mission_templates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MissionTemplate extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String content;
}
