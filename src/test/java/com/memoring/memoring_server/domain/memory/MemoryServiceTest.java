package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.Diary;
import com.memoring.memoring_server.domain.diary.DiaryImage;
import com.memoring.memoring_server.domain.diary.DiaryService;
import com.memoring.memoring_server.domain.diary.Emotion;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryWeeklyResponse;
import com.memoring.memoring_server.domain.memory.exception.MemoryNotFoundException;
import com.memoring.memoring_server.domain.mission.Mission;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.storage.StorageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemoryServiceTest {

    private static final ZoneId KST_ZONE = ZoneId.of("Asia/Seoul");

    @Mock
    private MemoryRepository memoryRepository;

    @Mock
    private DiaryService diaryService;

    @Mock
    private StorageService storageService;

    @Mock
    private UserService userService;

    @InjectMocks
    private MemoryService memoryService;

    @DisplayName("주차별 메모리를 조회한다")
    @Test
    void getWeeklyMemories() {
        User user = createUser(1L, "tester");
        Memory memory = createMemory(10L, user);
        Mission mission = createMission(100L, "mission");

        LocalDate targetDate = LocalDate.of(2025, 1, 15);
        LocalDate weekStart = LocalDate.of(2025, 1, 13);
        LocalDateTime startDateTime = weekStart.atStartOfDay().atZone(KST_ZONE).toLocalDateTime();
        LocalDateTime endDateTime = startDateTime.plusWeeks(1);

        Diary diary1 = createDiary(1L, user, memory, mission, "content-1", Emotion.HAPPY, startDateTime.plusDays(1));
        Diary diary2 = createDiary(2L, user, memory, mission, "content-2", Emotion.SAD, startDateTime.plusDays(2));

        DiaryImage image1 = createDiaryImage(1L, "key1", diary1);
        DiaryImage image2 = createDiaryImage(2L, "key2", diary2);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getDiariesByPeriod(eq(10L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(diary1, diary2));
        given(diaryService.getDiaryImageByDiaryId(1L)).willReturn(image1);
        given(diaryService.getDiaryImageByDiaryId(2L)).willReturn(image2);
        given(storageService.generatePresignedUrl("key1")).willReturn("url1");
        given(storageService.generatePresignedUrl("key2")).willReturn("url2");

        MemoryWeeklyResponse result = memoryService.getWeeklyMemories(10L, "tester", targetDate);

        assertThat(result.weekStartDate()).isEqualTo(weekStart);
        assertThat(result.weekEndDate()).isEqualTo(weekStart.plusDays(6));
        assertThat(result.thumbnails()).containsExactly("url1", "url2");
        assertThat(result.diaries()).hasSize(2);
        verify(diaryService).getDiariesByPeriod(eq(10L), eq(1L), eq(startDateTime), eq(endDateTime));
    }

    @DisplayName("주차별 메모리 조회 시 날짜가 null이면 현재 주차를 기준으로 조회한다")
    @Test
    void getWeeklyMemoriesWithNullDate() {
        User user = createUser(1L, "tester");
        Memory memory = createMemory(10L, user);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getDiariesByPeriod(eq(10L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        MemoryWeeklyResponse result = memoryService.getWeeklyMemories(10L, "tester", null);

        assertThat(result.weekStartDate()).isNotNull();
        assertThat(result.weekEndDate()).isEqualTo(result.weekStartDate().plusDays(6));
    }

    @DisplayName("주차별 메모리 조회 시 썸네일은 최대 3개까지만 반환된다")
    @Test
    void getWeeklyMemoriesLimitsThumbnailsToThree() {
        User user = createUser(1L, "tester");
        Memory memory = createMemory(10L, user);
        Mission mission = createMission(1L, "mission");

        Diary diary1 = createDiary(1L, user, memory, mission, "c1", Emotion.HAPPY, LocalDateTime.now());
        Diary diary2 = createDiary(2L, user, memory, mission, "c2", Emotion.SAD, LocalDateTime.now());
        Diary diary3 = createDiary(3L, user, memory, mission, "c3", Emotion.ANGRY, LocalDateTime.now());
        Diary diary4 = createDiary(4L, user, memory, mission, "c4", Emotion.NEUTRAL, LocalDateTime.now());

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getDiariesByPeriod(eq(10L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(diary1, diary2, diary3, diary4));
        given(diaryService.getDiaryImageByDiaryId(any(Long.class)))
                .willAnswer(invocation -> switch (invocation.getArgument(0, Long.class).intValue()) {
                    case 1 -> createDiaryImage(1L, "k1", diary1);
                    case 2 -> createDiaryImage(2L, "k2", diary2);
                    case 3 -> createDiaryImage(3L, "k3", diary3);
                    default -> createDiaryImage(4L, "k4", diary4);
                });
        given(storageService.generatePresignedUrl(any())).willReturn("url");

        MemoryWeeklyResponse result = memoryService.getWeeklyMemories(10L, "tester", LocalDate.now());

        assertThat(result.thumbnails()).hasSize(3);
        assertThat(result.diaries()).hasSize(4);
    }

    @DisplayName("존재하지 않는 메모리 조회 시 예외가 발생한다")
    @Test
    void getWeeklyMemoriesFailsWhenMemoryMissing() {
        User user = createUser(1L, "tester");
        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memoryService.getWeeklyMemories(999L, "tester", LocalDate.now()))
                .isInstanceOf(MemoryNotFoundException.class);
    }

    @DisplayName("다른 사용자의 메모리 조회 시 접근 거부 예외가 발생한다")
    @Test
    void getWeeklyMemoriesFailsWhenNotOwner() {
        User owner = createUser(1L, "owner");
        User other = createUser(2L, "other");
        Memory memory = createMemory(10L, owner);

        given(userService.getUserByUsername("other")).willReturn(other);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));

        assertThatThrownBy(() -> memoryService.getWeeklyMemories(10L, "other", LocalDate.now()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @DisplayName("사용자에게 메모리가 없으면 일기 생성 시 예외가 발생한다")
    @Test
    void createDiaryFailsWhenUserHasNoMemory() {
        User user = createUser(1L, "tester");
        DiaryCreateRequest request = new DiaryCreateRequest(10L, 1L, "content", Emotion.HAPPY);
        MultipartFile image = org.mockito.Mockito.mock(MultipartFile.class);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findByUser(user)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memoryService.createDiary(request, image, "tester"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("사용자가 메모리를 가지고 있지 않습니다.");
    }

    @DisplayName("사용자가 보유한 메모리로 일기를 생성한다")
    @Test
    void createDiary() {
        User user = createUser(1L, "tester");
        Memory memory = createMemory(10L, user);
        DiaryCreateRequest request = new DiaryCreateRequest(10L, 1L, "content", Emotion.HAPPY);
        MultipartFile image = org.mockito.Mockito.mock(MultipartFile.class);
        DiaryCreateResponse expected = new DiaryCreateResponse(123L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findByUser(user)).willReturn(Optional.of(memory));
        given(diaryService.createDiary(request, image, user, memory)).willReturn(expected);

        DiaryCreateResponse result = memoryService.createDiary(request, image, "tester");

        assertThat(result.diaryId()).isEqualTo(123L);
        verify(diaryService).createDiary(request, image, user, memory);
    }

    private User createUser(Long id, String username) {
        User user = User.create("nickname", username, "password");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Memory createMemory(Long id, User user) {
        Memory memory = Memory.create(user, "memory");
        ReflectionTestUtils.setField(memory, "id", id);
        return memory;
    }

    private Mission createMission(Long id, String content) {
        Mission mission = Mission.create(content);
        ReflectionTestUtils.setField(mission, "id", id);
        return mission;
    }

    private Diary createDiary(Long id, User user, Memory memory, Mission mission, String content, Emotion mood, LocalDateTime createdAt) {
        Diary diary = Diary.create(user, memory, mission, content, mood);
        ReflectionTestUtils.setField(diary, "id", id);
        ReflectionTestUtils.setField(diary, "createdAt", createdAt);
        return diary;
    }

    private DiaryImage createDiaryImage(Long id, String key, Diary diary) {
        DiaryImage image = DiaryImage.create(key, 1024L, diary);
        ReflectionTestUtils.setField(image, "id", id);
        return image;
    }
}
