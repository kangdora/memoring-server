package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.*;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummary;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.domain.memory.exception.MemoryNotFoundException;
import com.memoring.memoring_server.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemoryService {

    private final MemoryRepository memoryRepository;
    private final DiaryService diaryService;
    private final StorageService storageService;
    private final UserService userService;

    public List<MemoryDiarySummary> getRecentMemories(Long memoryId, String username) {
        User user = userService.getUserByUsername(username);
        validateMemory(memoryId, username);

        List<Diary> diaries = diaryService.getRecentDiaries(memoryId, user.getId());

        return diaries.stream()
                .map(this::toSummaryDto)
                .toList();
    }

    public List<MemoryDiaryResponse> getMemories(Long memoryId, String username) {
        User user = userService.getUserByUsername(username);
        validateMemory(memoryId, username);

        List<Diary> diaries = diaryService.getDiaries(memoryId, user.getId());

        return diaries.stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional
    public DiaryCreateResponse createDiary(
            DiaryCreateRequest request,
            MultipartFile image,
            String username
    ) {
        User user = userService.getUserByUsername(username);

        Memory memory = memoryRepository.findByUser(user)
                .orElseThrow(() ->
                        new IllegalStateException("유저가 메모리를 가지고 있지 않습니다.")
                );

        return diaryService.createDiary(request, image, user, memory);
    }

    private void validateMemory(Long memoryId, String username) {
        User user = userService.getUserByUsername(username);
        Memory memory = memoryRepository.findById(memoryId)
                .orElseThrow(MemoryNotFoundException::new);

        if (!memory.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("해당 메모리에 대한 권한이 없습니다.");
        }
    }

    private MemoryDiarySummary toSummaryDto(Diary diary) {
        return new MemoryDiarySummary(
                diary.getId(),
                extractDate(diary.getCreatedAt()),
                getImageUrl(diary.getId()),
                diary.getContent()
        );
    }

    private MemoryDiaryResponse toResponseDto(Diary diary) {
        return new MemoryDiaryResponse(
                diary.getId(),
                extractDate(diary.getCreatedAt()),
                getImageUrl(diary.getId()),
                diary.getContent(),
                diary.getMood()
        );
    }

    private LocalDate extractDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.toLocalDate();
    }

    private String getImageUrl(Long diaryId) {
        DiaryImage image = diaryService.getDiaryImageByDiaryId(diaryId);
        return storageService.generatePresignedUrl(image.getS3key());
    }
}
