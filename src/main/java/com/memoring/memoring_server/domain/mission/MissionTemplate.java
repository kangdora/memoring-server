package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mission_templates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MissionTemplate extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String title;

    @Column(columnDefinition="text")
    private String description;

    @Column(length=200)
    private String tags; // 선택 사항, 주제 분류 등
}