package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.Diary;
import com.memoring.memoring_server.domain.diary.DiaryImage;
import com.memoring.memoring_server.domain.diary.DiaryImageRepository;
import com.memoring.memoring_server.domain.diary.DiaryRepository;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponseDto;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummaryDto;
import com.memoring.memoring_server.global.exception.MemoryNotFoundException;
import com.memoring.memoring_server.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final StorageService storageService;

    public List<MemoryDiarySummaryDto> getRecentMemories(Long memoryId) {
        validateMemory(memoryId);
        List<Diary> diaries = diaryRepository.findTop3ByMemoryIdOrderByCreatedAtDesc(memoryId);
        return diaries.stream()
                .map(this::toSummaryDto)
                .toList();
    }

    public List<MemoryDiaryResponseDto> getMemories(Long memoryId) {
        validateMemory(memoryId);
        List<Diary> diaries = diaryRepository.findAllByMemoryIdOrderByCreatedAtDesc(memoryId);
        return diaries.stream()
                .map(this::toResponseDto)
                .toList();
    }

    private void validateMemory(Long memoryId) {
        if (!memoryRepository.existsById(memoryId)) {
            throw new MemoryNotFoundException();
        }
    }

    private MemoryDiarySummaryDto toSummaryDto(Diary diary) {
        return new MemoryDiarySummaryDto(
                diary.getId(),
                extractDate(diary.getCreatedAt()),
                getImageUrl(diary.getId()),
                diary.getContent()
        );
    }

    private MemoryDiaryResponseDto toResponseDto(Diary diary) {
        return new MemoryDiaryResponseDto(
                diary.getId(),
                extractDate(diary.getCreatedAt()),
                getImageUrl(diary.getId()),
                diary.getContent(),
                diary.getMood()
        );
    }

    private LocalDate extractDate(LocalDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(LocalDateTime::toLocalDate)
                .orElse(null);
    }

    private String getImageUrl(Long diaryId) {
        return diaryImageRepository.findByDiaryId(diaryId)
                .map(DiaryImage::getS3key)
                .map(storageService::generatePresignedUrl)
                .orElse(null);
    }
}
