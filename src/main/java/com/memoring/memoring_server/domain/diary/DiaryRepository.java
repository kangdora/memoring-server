package com.memoring.memoring_server.domain.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findTop3ByMemoryIdAndUserIdOrderByCreatedAtDesc(
            Long memoryId,
            Long userId
    );

    List<Diary> findAllByMemoryIdAndUserIdOrderByCreatedAtDesc(
            Long memoryId,
            Long userId
    );

    List<Diary> findAllByMemoryIdAndUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
            Long memoryId,
            Long userId,
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    );

    boolean existsByUserIdAndMissionId(Long userId, Long missionId);
}
