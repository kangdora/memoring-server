package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="weekly_mission_options",
        uniqueConstraints=@UniqueConstraint(name="uk_week_option_order",
                columnNames={"weekly_mission_set_id","orderNo"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WeeklyMissionOption extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private WeeklyMissionSet weeklyMissionSet;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private MissionTemplate missionTemplate;

    @Column(nullable=false)
    private Integer orderNo; // 1..3
}
