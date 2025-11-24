package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.mission.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findByUserMission(UserMission userMission);
}