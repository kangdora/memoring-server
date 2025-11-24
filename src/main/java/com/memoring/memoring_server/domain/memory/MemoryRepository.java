package com.memoring.memoring_server.domain.memory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
}
