package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequestDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponseDto;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponseDto;
import com.memoring.memoring_server.domain.memory.Memory;
import com.memoring.memoring_server.domain.memory.MemoryRepository;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.mission.MissionRepository;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.global.exception.DiaryOwnershipMismatchException;
import com.memoring.memoring_server.global.exception.MemoryNotFoundException;
import com.memoring.memoring_server.global.exception.MissionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemoryRepository memoryRepository;
    private final MissionRepository missionRepository;
    private final DiaryImageRepository diaryImageRepository;

    @Transactional
    public DiaryCreateResponseDto createDiary(DiaryCreateRequestDto dto) {
        Memory memory = memoryRepository.findById(dto.memoryId())
                .orElseThrow(MemoryNotFoundException::new);
        Mission mission = missionRepository.findById(dto.missionId())
                .orElseThrow(MissionNotFoundException::new);

        User user = mission.getUser();
        if (memory.getUser() != null && !memory.getUser().getId().equals(user.getId())) {
            throw new DiaryOwnershipMismatchException();
        }

        Diary diary = Diary.create(user, memory, mission, dto.content(), dto.mood());
        Diary savedDiary = diaryRepository.save(diary);
        return new DiaryCreateResponseDto(savedDiary.getId());
    }

    public Optional<DiaryDetailResponseDto> getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .map(diary -> new DiaryDetailResponseDto(
                        Optional.ofNullable(diary.getCreatedAt())
                                .map(LocalDateTime::toLocalDate)
                                .orElse(null),
                        diaryImageRepository.findByDiaryId(diaryId)
                                .map(DiaryImage::getS3key)
                                .orElse(null),
                        diary.getMission().getContent(),
                        diary.getContent(),
                        diary.getMood()
                ));
    }

    @Transactional
    public boolean deleteDiary(Long diaryId) {  // 예외처리 예정
        if (!diaryRepository.existsById(diaryId)) {
            return false;
        }
        diaryRepository.deleteById(diaryId);
        return true;
    }
}