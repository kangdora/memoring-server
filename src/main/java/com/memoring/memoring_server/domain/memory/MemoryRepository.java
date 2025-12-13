package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    Optional<Memory> findByUser(User user);
}
