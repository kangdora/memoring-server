package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.caregiver.CareRelationService;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponse;
import com.memoring.memoring_server.domain.memory.Memory;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.mission.MissionService;
import com.memoring.memoring_server.domain.mission.UserMission;
import com.memoring.memoring_server.domain.user.Role;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.diary.exception.DiaryNotFoundException;
import com.memoring.memoring_server.domain.diary.exception.DiaryOwnershipMismatchException;
import com.memoring.memoring_server.domain.mission.exception.MissionNotFoundException;
import com.memoring.memoring_server.domain.diary.exception.DiaryImageMissingException;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.external.openai.stt.SttService;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import com.memoring.memoring_server.global.storage.StorageService;
import com.memoring.memoring_server.global.storage.dto.FileDeleteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final DiaryImageRepository diaryImageRepository;
    private final MissionService missionService;
    private final StorageService storageService;
    private final SttService sttService;
    private final CareRelationService careRelationService;
    private final UserService userService;

    @Transactional
    public DiaryCreateResponse createDiary(
            DiaryCreateRequest request,
            MultipartFile image,
            User user,
            Memory memory
    ) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("일기 이미지는 필수입니다.");
        }

        UserMission userMission = missionService.getUserMissionById(request.missionId());

        if (!userMission.getUser().getId().equals(user.getId())) {
            throw new DiaryOwnershipMismatchException();
        }

        Mission mission = Optional.ofNullable(userMission.getMission())
                .orElseThrow(MissionNotFoundException::new);

        Diary diary = Diary.create(user, memory, mission, request.content(), request.mood());
        Diary savedDiary = diaryRepository.save(diary);

        String key = uploadImage(savedDiary, image);
        diaryImageRepository.save(
                DiaryImage.create(key, image.getSize(), savedDiary)
        );

        return new DiaryCreateResponse(savedDiary.getId());
    }

    private String uploadImage(Diary diary, MultipartFile image) {
        String ext = Optional.ofNullable(image.getOriginalFilename())
                .filter(name -> name.contains("."))
                .map(name -> name.substring(name.lastIndexOf(".")))
                .orElse("");

        String key = "diary/" + diary.getId() + "/" + UUID.randomUUID() + ext;
        storageService.uploadFile(image, key);
        return key;
    }


    public Optional<DiaryDetailResponse> getDiary(Long diaryId, String username) {
        return diaryRepository.findById(diaryId)
                .map(diary -> {
                    validateDiaryOwnership(diary, username);
                    return diary;
                })
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

    public List<Diary> getDiariesByPeriod(
            Long memoryId,
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return diaryRepository
                .findAllByMemoryIdAndUserIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtDesc(
                        memoryId,
                        userId,
                        start,
                        end
                );
    }

    @Transactional
    public boolean deleteDiary(Long diaryId, String username) {  // 예외처리 예정
        Optional<Diary> diaryOptional = diaryRepository.findById(diaryId);

        if (diaryOptional.isEmpty()) {
            return false;
        }
        Diary diary = diaryOptional.get();
        validateDiaryOwnership(diary, username);

        diaryImageRepository.findByDiaryId(diaryId)
                .ifPresent(image -> {
                    storageService.deleteFile(new FileDeleteRequest(image.getS3key()));
                    diaryImageRepository.delete(image);
                });

        diaryRepository.delete(diary);
        return true;
    }

    public Diary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);
    }

    public DiaryImage getDiaryImageByDiaryId(Long diaryId) {
        return diaryImageRepository.findByDiaryId(diaryId)
                .orElseThrow(DiaryImageMissingException::new);
    }

    public SttTranscriptionResponse transcribeDiaryAudio(MultipartFile file) {
        return sttService.transcribe(file);
    }

    private void validateDiaryOwnership(Diary diary, String username) {
        if (diary.getUser().getUsername().equals(username)) {
            return;
        }

        User requester = userService.getUserByUsername(username);
        if (Role.CAREGIVER.equals(requester.getRole())
                && careRelationService.isConnected(diary.getUser().getId(), requester.getId())) {
            return;
        }

        throw new AccessDeniedException("해당 일기에 대한 권한이 없습니다.");
    }
}
