package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {
    Optional<UserMission> findByUser(User user);
}