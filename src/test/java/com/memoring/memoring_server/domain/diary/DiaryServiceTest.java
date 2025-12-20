package com.memoring.memoring_server.domain.diary;

import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.diary.dto.DiaryDetailResponse;
import com.memoring.memoring_server.domain.diary.exception.DiaryImageMissingException;
import com.memoring.memoring_server.domain.diary.exception.DiaryNotFoundException;
import com.memoring.memoring_server.domain.diary.exception.DiaryOwnershipMismatchException;
import com.memoring.memoring_server.domain.memory.Memory;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.mission.MissionService;
import com.memoring.memoring_server.domain.mission.UserMission;
import com.memoring.memoring_server.domain.mission.exception.MissionNotFoundException;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.global.storage.StorageService;
import com.memoring.memoring_server.global.storage.dto.FileDeleteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private DiaryImageRepository diaryImageRepository;

    @Mock
    private MissionService missionService;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private DiaryService diaryService;

    @DisplayName("일기를 생성하고 이미지를 업로드한다")
    @Test
    void createDiary() {
        User user = createUser("tester");
        Memory memory = createMemory(user);
        Mission mission = Mission.create("mission");
        UserMission userMission = UserMission.create(user, mission);
        DiaryCreateRequest request = new DiaryCreateRequest(1L, 2L, "content", Emotion.HAPPY);
        MultipartFile image = mock(MultipartFile.class);

        given(image.isEmpty()).willReturn(false);
        given(image.getOriginalFilename()).willReturn("image.png");
        given(image.getSize()).willReturn(123L);
        given(missionService.getUserMissionById(2L)).willReturn(userMission);
        given(diaryRepository.save(any(Diary.class))).willAnswer(invocation -> {
            Diary diary = invocation.getArgument(0);
            ReflectionTestUtils.setField(diary, "id", 1L);
            return diary;
        });

        DiaryCreateResponse response = diaryService.createDiary(request, image, user, memory);

        assertThat(response.diaryId()).isEqualTo(1L);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(storageService).uploadFile(eq(image), keyCaptor.capture());
        assertThat(keyCaptor.getValue()).startsWith("diary/1/");
        verify(diaryImageRepository).save(any(DiaryImage.class));
    }

    @DisplayName("이미지가 없으면 일기 생성 시 예외가 발생한다")
    @Test
    void createDiaryFailsWhenImageMissing() {
        User user = createUser("tester");
        Memory memory = createMemory(user);
        MultipartFile image = mock(MultipartFile.class);
        given(image.isEmpty()).willReturn(true);

        DiaryCreateRequest request = new DiaryCreateRequest(1L, 2L, "content", Emotion.HAPPY);

        assertThatThrownBy(() -> diaryService.createDiary(request, image, user, memory))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("다른 사용자의 미션으로 일기 생성 시 예외가 발생한다")
    @Test
    void createDiaryFailsWhenOwnershipMismatch() {
        User owner = createUser("owner");
        User other = createUser("other");
        ReflectionTestUtils.setField(other, "id", 2L);
        Memory memory = createMemory(owner);
        Mission mission = Mission.create("mission");
        UserMission userMission = UserMission.create(other, mission);
        MultipartFile image = mock(MultipartFile.class);
        given(image.isEmpty()).willReturn(false);
        given(missionService.getUserMissionById(2L)).willReturn(userMission);

        DiaryCreateRequest request = new DiaryCreateRequest(1L, 2L, "content", Emotion.HAPPY);

        assertThatThrownBy(() -> diaryService.createDiary(request, image, owner, memory))
                .isInstanceOf(DiaryOwnershipMismatchException.class);
    }

    @DisplayName("미션이 없으면 일기 생성 시 예외가 발생한다")
    @Test
    void createDiaryFailsWhenMissionMissing() {
        User user = createUser("tester");
        Memory memory = createMemory(user);
        UserMission userMission = UserMission.create(user, null);
        MultipartFile image = mock(MultipartFile.class);
        given(image.isEmpty()).willReturn(false);
        given(missionService.getUserMissionById(2L)).willReturn(userMission);

        DiaryCreateRequest request = new DiaryCreateRequest(1L, 2L, "content", Emotion.HAPPY);

        assertThatThrownBy(() -> diaryService.createDiary(request, image, user, memory))
                .isInstanceOf(MissionNotFoundException.class);
    }

    @DisplayName("일기를 상세 조회한다")
    @Test
    void getDiary() {
        User user = createUser("tester");
        Mission mission = Mission.create("mission");
        Memory memory = createMemory(user);
        Diary diary = Diary.create(user, memory, mission, "content", Emotion.HAPPY);
        ReflectionTestUtils.setField(diary, "createdAt", LocalDateTime.of(2024, 1, 1, 0, 0));
        DiaryImage image = DiaryImage.create("key", 1L, diary);

        given(diaryRepository.findById(1L)).willReturn(Optional.of(diary));
        given(diaryImageRepository.findByDiaryId(1L)).willReturn(Optional.of(image));
        given(storageService.generatePresignedUrl("key")).willReturn("url");

        Optional<DiaryDetailResponse> response = diaryService.getDiary(1L, "tester");

        assertThat(response).isPresent();
        assertThat(response.get().imageUrl()).isEqualTo("url");
    }

    @DisplayName("존재하지 않는 일기 삭제 시 false를 반환한다")
    @Test
    void deleteDiaryReturnsFalseWhenMissing() {
        assertThat(diaryService.deleteDiary(1L, "tester")).isFalse();
    }

    @DisplayName("다른 사용자의 일기 삭제 시 예외가 발생한다")
    @Test
    void deleteDiaryFailsWhenNotOwner() {
        User user = createUser("owner");
        User other = createUser("other");
        Mission mission = Mission.create("mission");
        Diary diary = Diary.create(user, createMemory(user), mission, "content", Emotion.HAPPY);

        given(diaryRepository.findById(1L)).willReturn(Optional.of(diary));

        assertThatThrownBy(() -> diaryService.deleteDiary(1L, other.getUsername()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("일기를 삭제하고 이미지도 제거한다")
    @Test
    void deleteDiary() {
        User user = createUser("owner");
        Mission mission = Mission.create("mission");
        Diary diary = Diary.create(user, createMemory(user), mission, "content", Emotion.HAPPY);
        DiaryImage image = DiaryImage.create("s3key", 10L, diary);

        given(diaryRepository.findById(1L)).willReturn(Optional.of(diary));
        given(diaryImageRepository.findByDiaryId(1L)).willReturn(Optional.of(image));

        boolean result = diaryService.deleteDiary(1L, user.getUsername());

        assertThat(result).isTrue();
        verify(storageService).deleteFile(new FileDeleteRequest("s3key"));
        verify(diaryRepository).delete(diary);
    }

    @DisplayName("일기 이미지가 없으면 예외를 던진다")
    @Test
    void getDiaryImageFailsWhenMissing() {
        given(diaryImageRepository.findByDiaryId(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> diaryService.getDiaryImageByDiaryId(1L))
                .isInstanceOf(DiaryImageMissingException.class);
    }

    @DisplayName("일기가 없으면 조회 시 예외를 던진다")
    @Test
    void getDiaryByIdFailsWhenMissing() {
        given(diaryRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> diaryService.getDiaryById(1L))
                .isInstanceOf(DiaryNotFoundException.class);
    }

    private User createUser(String username) {
        User user = User.create("nick", username, "pass");
        ReflectionTestUtils.setField(user, "id", 1L);
        return user;
    }

    private Memory createMemory(User user) {
        Memory memory = Memory.create(user, "memory");
        ReflectionTestUtils.setField(memory, "id", 1L);
        return memory;
    }
}
