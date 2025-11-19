package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.mission.UserMission;
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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_mission_id", nullable = false, unique = true)
    private UserMission userMission;

    @Column(columnDefinition = "text", nullable = false)
    private String s3key;

    @Column(nullable = false)
    private Long sizeBytes;

    public static Record create(UserMission userMission, String s3key, Long sizeBytes) {
        Record record = new Record();
        record.userMission = userMission;
        record.s3key = s3key;
        record.sizeBytes = sizeBytes;
        return record;
    }

    public void update(String s3key, Long sizeBytes) { // 업데이트 용
        this.s3key = s3key;
        this.sizeBytes = sizeBytes;
    }
}
