package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false, unique = true)
    private User user;

    @Column(columnDefinition = "text", nullable = false)
    private String s3key;

    @Column(nullable = false)
    private Long sizeBytes;

    public static Record create(Mission mission, User user, String s3key, Long sizeBytes) {
        Record record = new Record();
        record.mission = mission;
        record.user = user;
        record.s3key = s3key;
        record.sizeBytes = sizeBytes;
        return record;
    }
}
