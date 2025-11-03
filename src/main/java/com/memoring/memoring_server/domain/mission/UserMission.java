package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="user_missions",
        uniqueConstraints=@UniqueConstraint(name="uk_user_week",
                columnNames={"user_id","year","weekOfYear"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserMission extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private User user;

    @Column(nullable=false)
    private Integer year;

    @Column(nullable=false)
    private Integer weekOfYear;

    @ManyToOne(fetch = FetchType.LAZY)
    private MissionTemplate selectedTemplate;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private MissionStatus status;
}
