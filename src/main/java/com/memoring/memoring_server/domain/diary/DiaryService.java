package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponse;
import com.memoring.memoring_server.domain.memory.Memory;
import com.memoring.memoring_server.domain.memory.MemoryRepository;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.mission.UserMission;
import com.memoring.memoring_server.domain.mission.UserMissionRepository;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.global.exception.DiaryOwnershipMismatchException;
import com.memoring.memoring_server.global.exception.MemoryNotFoundException;
import com.memoring.memoring_server.global.exception.DiaryNotFoundException;
import com.memoring.memoring_server.global.exception.MissionNotFoundException;
import com.memoring.memoring_server.global.external.openai.stt.SttService;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import com.memoring.memoring_server.global.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemoryRepository memoryRepository;
    private final UserMissionRepository userMissionRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final StorageService storageService;
    private final SttService sttService;

    @Transactional
    public DiaryCreateResponse createDiary(DiaryCreateRequest request) {
        Memory memory = memoryRepository.findById(request.memoryId())
                .orElseThrow(MemoryNotFoundException::new);
        UserMission userMission = userMissionRepository.findById(request.missionId())
                .orElseThrow(MissionNotFoundException::new);

        User user = userMission.getUser();
        if (memory.getUser() != null && !memory.getUser().getId().equals(user.getId())) {
            throw new DiaryOwnershipMismatchException();
        }

        Mission mission = Optional.ofNullable(userMission.getMission())
                .orElseThrow(MissionNotFoundException::new);
        Diary diary = Diary.create(user, memory, mission, request.content(), request.mood());
        Diary savedDiary = diaryRepository.save(diary);
        return new DiaryCreateResponse(savedDiary.getId());
    }

    @Transactional
    public void uploadDiaryImage(Long diaryId, MultipartFile file) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);

        String imageS3Key = storageService.uploadDiaryImage(diaryId, file);

        diaryImageRepository.findByDiaryId(diaryId)
                .ifPresentOrElse(
                        image -> image.update(imageS3Key, file.getSize()),
                        () -> diaryImageRepository.save(DiaryImage.create(imageS3Key, file.getSize(), diary))
                );
    }

    public Optional<DiaryDetailResponse> getDiary(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .map(diary -> new DiaryDetailResponse(
                        Optional.ofNullable(diary.getCreatedAt())
                                .map(LocalDateTime::toLocalDate)
                                .orElse(null),
                        diaryImageRepository.findByDiaryId(diaryId)
                                .map(DiaryImage::getS3key)
                                .map(storageService::generatePresignedUrl)
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

    public SttTranscriptionResponse transcribeDiaryAudio(MultipartFile file) {
        return sttService.transcribe(file);
    }
}