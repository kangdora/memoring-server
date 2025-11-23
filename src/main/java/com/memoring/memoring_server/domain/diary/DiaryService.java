package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.*;
import com.memoring.memoring_server.domain.memory.Memory;
import com.memoring.memoring_server.domain.memory.MemoryRepository;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.mission.UserMission;
import com.memoring.memoring_server.domain.mission.UserMissionRepository;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.DiaryOwnershipMismatchException;
import com.memoring.memoring_server.global.exception.MemoryNotFoundException;
import com.memoring.memoring_server.global.exception.DiaryNotFoundException;
import com.memoring.memoring_server.global.exception.MissionNotFoundException;
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
import java.util.Optional;
import java.util.UUID;

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
    private final UserService userService;

    @Transactional
    public DiaryCreateResponse createDiary(DiaryCreateRequest request, String username) {
        Memory memory = memoryRepository.findById(request.memoryId())
                .orElseThrow(MemoryNotFoundException::new);
        UserMission userMission = userMissionRepository.findById(request.missionId())
                .orElseThrow(MissionNotFoundException::new);

        User user = userService.getUserByUsername(username);
        if (memory.getUser() != null && !memory.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("일기 작성 권한이 없습니다.");
        }

        if (!userMission.getUser().getId().equals(user.getId())) {
            throw new DiaryOwnershipMismatchException();
        }

        Mission mission = Optional.ofNullable(userMission.getMission())
                .orElseThrow(MissionNotFoundException::new);
        Diary diary = Diary.create(user, memory, mission, request.content(), request.mood());
        Diary savedDiary = diaryRepository.save(diary);
        return new DiaryCreateResponse(savedDiary.getId());
    }

    @Transactional
    public DiaryImagePresignedUrlResponse createDiaryImagePresignedUrl(
            Long diaryId,
            DiaryImagePresignedUrlRequest request,
            String username
    ) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(DiaryNotFoundException::new);

        validateDiaryOwnership(diary, username);

        // 클라이언트가 업로드할 이미지 파일을 그대로 S3에 PUT할 수 있도록 서버에서 S3 키를 선생성한다.
        // presigned URL 발급 시점에 키와 Content-Type을 확정해야 하므로 여기서 파일명을 기반으로 키를 만든다.
        String originalFilename = request.fileName();
        String ext = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String key = "diary/" + diaryId + "/" + UUID.randomUUID() + ext;

        // 생성한 S3 키와 Content-Type을 이용해 제한된 시간 동안만 유효한 업로드 전용 presigned URL을 만든다.
        // 이 URL을 받은 클라이언트는 서버를 거치지 않고 S3에 직접 PUT 요청을 보낼 수 있다.
        String uploadUrl = storageService.generateUploadPresignedUrl(key, request.contentType());
        Long fileSizeBytes = Optional.ofNullable(request.fileSizeBytes())
                .orElseThrow(() -> new IllegalArgumentException("fileSizeBytes is required"));
        diaryImageRepository.findByDiaryId(diaryId)
                .ifPresentOrElse(
                        image -> image.update(key, fileSizeBytes),
                        () -> diaryImageRepository.save(DiaryImage.create(key, fileSizeBytes, diary))
                );

        return new DiaryImagePresignedUrlResponse(uploadUrl, key);
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

    public SttTranscriptionResponse transcribeDiaryAudio(MultipartFile file) {
        return sttService.transcribe(file);
    }

    private void validateDiaryOwnership(Diary diary, String username) {
        if (!diary.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("해당 일기에 대한 권한이 없습니다.");
        }
    }
}
