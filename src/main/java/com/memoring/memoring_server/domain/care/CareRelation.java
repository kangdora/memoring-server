package com.memoring.memoring_server.domain.care;

import com.memoring.memoring_server.domain.common.AuditableEntity;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="care_relations",
        uniqueConstraints = @UniqueConstraint(name="uk_care_pair",
                columnNames={"patient_id","caregiver_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CareRelation extends AuditableEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private User patient;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    private User caregiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private CareRelationStatus status;
}
