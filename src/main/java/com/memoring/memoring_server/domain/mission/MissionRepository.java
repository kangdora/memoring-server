package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
}
