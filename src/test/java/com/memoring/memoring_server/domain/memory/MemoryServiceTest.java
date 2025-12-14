package com.memoring.memoring_server.domain.memory;

import com.memoring.memoring_server.domain.diary.*;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateRequest;
import com.memoring.memoring_server.domain.diary.dto.DiaryCreateResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiaryResponse;
import com.memoring.memoring_server.domain.memory.dto.MemoryDiarySummary;
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

    @DisplayName("최근 메모리 3개를 조회한다")
    @Test
    void getRecentMemories() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Memory memory = Memory.create(user, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        Mission mission = Mission.create("미션 내용");
        ReflectionTestUtils.setField(mission, "id", 100L);

        Diary diary1 = createDiary(1L, user, memory, mission, "일기1", Emotion.HAPPY);
        Diary diary2 = createDiary(2L, user, memory, mission, "일기2", Emotion.SAD);
        Diary diary3 = createDiary(3L, user, memory, mission, "일기3", Emotion.ANGRY);

        DiaryImage image1 = createDiaryImage(1L, "key1", diary1);
        DiaryImage image2 = createDiaryImage(2L, "key2", diary2);
        DiaryImage image3 = createDiaryImage(3L, "key3", diary3);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getRecentDiaries(10L, 1L)).willReturn(List.of(diary1, diary2, diary3));
        given(diaryService.getDiaryImageByDiaryId(1L)).willReturn(image1);
        given(diaryService.getDiaryImageByDiaryId(2L)).willReturn(image2);
        given(diaryService.getDiaryImageByDiaryId(3L)).willReturn(image3);
        given(storageService.generatePresignedUrl("key1")).willReturn("url1");
        given(storageService.generatePresignedUrl("key2")).willReturn("url2");
        given(storageService.generatePresignedUrl("key3")).willReturn("url3");

        // when
        List<MemoryDiarySummary> result = memoryService.getRecentMemories(10L, "tester");

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).diaryId()).isEqualTo(1L);
        assertThat(result.get(0).imageUrl()).isEqualTo("url1");
        assertThat(result.get(0).content()).isEqualTo("일기1");
    }

    @DisplayName("메모리가 존재하지 않으면 예외가 발생한다")
    @Test
    void getRecentMemoriesFailsWhenMemoryNotFound() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(999L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memoryService.getRecentMemories(999L, "tester"))
                .isInstanceOf(MemoryNotFoundException.class);
    }

    @DisplayName("다른 사용자의 메모리에 접근하면 예외가 발생한다")
    @Test
    void getRecentMemoriesFailsWhenNotOwner() {
        // given
        User owner = User.create("owner", "owner", "password");
        ReflectionTestUtils.setField(owner, "id", 1L);

        User other = User.create("other", "other", "password");
        ReflectionTestUtils.setField(other, "id", 2L);

        Memory memory = Memory.create(owner, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        given(userService.getUserByUsername("other")).willReturn(other);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));

        // when & then
        assertThatThrownBy(() -> memoryService.getRecentMemories(10L, "other"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("해당 메모리에 대한 권한이 없습니다.");
    }

    @DisplayName("주차별 메모리를 조회한다")
    @Test
    void getWeeklyMemories() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Memory memory = Memory.create(user, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        Mission mission = Mission.create("미션 내용");
        ReflectionTestUtils.setField(mission, "id", 100L);

        // 2025-01-13 (월요일)을 기준으로 테스트
        LocalDate targetDate = LocalDate.of(2025, 1, 15); // 수요일
        LocalDate weekStart = LocalDate.of(2025, 1, 13); // 월요일
        LocalDateTime startDateTime = weekStart.atStartOfDay().atZone(KST_ZONE).toLocalDateTime();
        LocalDateTime endDateTime = startDateTime.plusWeeks(1);

        Diary diary1 = createDiary(1L, user, memory, mission, "일기1", Emotion.HAPPY);
        Diary diary2 = createDiary(2L, user, memory, mission, "일기2", Emotion.SAD);
        ReflectionTestUtils.setField(diary1, "createdAt", startDateTime.plusDays(1));
        ReflectionTestUtils.setField(diary2, "createdAt", startDateTime.plusDays(2));

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

        // when
        MemoryWeeklyResponse result = memoryService.getWeeklyMemories(10L, "tester", targetDate);

        // then
        assertThat(result.weekStartDate()).isEqualTo(weekStart);
        assertThat(result.weekEndDate()).isEqualTo(weekStart.plusDays(6));
        assertThat(result.thumbnails()).hasSize(2);
        assertThat(result.thumbnails()).containsExactly("url1", "url2");
        assertThat(result.diaries()).hasSize(2);
        assertThat(result.diaries().get(0).diaryId()).isEqualTo(1L);
        assertThat(result.diaries().get(1).diaryId()).isEqualTo(2L);

        verify(diaryService).getDiariesByPeriod(eq(10L), eq(1L), eq(startDateTime), eq(endDateTime));
    }

    @DisplayName("주차별 메모리 조회 시 날짜가 null이면 현재 주차를 조회한다")
    @Test
    void getWeeklyMemoriesWithNullDate() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Memory memory = Memory.create(user, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getDiariesByPeriod(eq(10L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of());

        // when
        MemoryWeeklyResponse result = memoryService.getWeeklyMemories(10L, "tester", null);

        // then
        assertThat(result).isNotNull();
        assertThat(result.weekStartDate()).isNotNull();
        assertThat(result.weekEndDate()).isNotNull();
    }

    @DisplayName("주차별 메모리 조회 시 썸네일은 최대 3개만 반환된다")
    @Test
    void getWeeklyMemoriesLimitsThumbnailsToThree() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Memory memory = Memory.create(user, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        Mission mission = Mission.create("미션 내용");

        LocalDate targetDate = LocalDate.of(2025, 1, 15);

        Diary diary1 = createDiary(1L, user, memory, mission, "일기1", Emotion.HAPPY);
        Diary diary2 = createDiary(2L, user, memory, mission, "일기2", Emotion.SAD);
        Diary diary3 = createDiary(3L, user, memory, mission, "일기3", Emotion.ANGRY);
        Diary diary4 = createDiary(4L, user, memory, mission, "일기4", Emotion.NEUTRAL);
        Diary diary5 = createDiary(5L, user, memory, mission, "일기5", Emotion.HAPPY);

        DiaryImage image1 = createDiaryImage(1L, "key1", diary1);
        DiaryImage image2 = createDiaryImage(2L, "key2", diary2);
        DiaryImage image3 = createDiaryImage(3L, "key3", diary3);
        DiaryImage image4 = createDiaryImage(4L, "key4", diary4);
        DiaryImage image5 = createDiaryImage(5L, "key5", diary5);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getDiariesByPeriod(eq(10L), eq(1L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .willReturn(List.of(diary1, diary2, diary3, diary4, diary5));
        given(diaryService.getDiaryImageByDiaryId(1L)).willReturn(image1);
        given(diaryService.getDiaryImageByDiaryId(2L)).willReturn(image2);
        given(diaryService.getDiaryImageByDiaryId(3L)).willReturn(image3);
        given(diaryService.getDiaryImageByDiaryId(4L)).willReturn(image4);
        given(diaryService.getDiaryImageByDiaryId(5L)).willReturn(image5);
        given(storageService.generatePresignedUrl(any())).willReturn("url");

        // when
        MemoryWeeklyResponse result = memoryService.getWeeklyMemories(10L, "tester", targetDate);

        // then
        assertThat(result.thumbnails()).hasSize(3);
        assertThat(result.diaries()).hasSize(5);
    }

    @DisplayName("모든 메모리를 조회한다")
    @Test
    void getMemories() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Memory memory = Memory.create(user, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        Mission mission = Mission.create("미션 내용");

        Diary diary1 = createDiary(1L, user, memory, mission, "일기1", Emotion.HAPPY);
        Diary diary2 = createDiary(2L, user, memory, mission, "일기2", Emotion.SAD);

        DiaryImage image1 = createDiaryImage(1L, "key1", diary1);
        DiaryImage image2 = createDiaryImage(2L, "key2", diary2);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findById(10L)).willReturn(Optional.of(memory));
        given(diaryService.getDiaries(10L, 1L)).willReturn(List.of(diary1, diary2));
        given(diaryService.getDiaryImageByDiaryId(1L)).willReturn(image1);
        given(diaryService.getDiaryImageByDiaryId(2L)).willReturn(image2);
        given(storageService.generatePresignedUrl("key1")).willReturn("url1");
        given(storageService.generatePresignedUrl("key2")).willReturn("url2");

        // when
        List<MemoryDiaryResponse> result = memoryService.getMemories(10L, "tester");

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).diaryId()).isEqualTo(1L);
        assertThat(result.get(0).imageUrl()).isEqualTo("url1");
        assertThat(result.get(0).content()).isEqualTo("일기1");
        assertThat(result.get(0).mood()).isEqualTo(Emotion.HAPPY);
        assertThat(result.get(1).mood()).isEqualTo(Emotion.SAD);
    }

    @DisplayName("일기 생성 시 사용자가 메모리를 가지고 있지 않으면 예외가 발생한다")
    @Test
    void createDiaryFailsWhenUserHasNoMemory() {
        // given
        User user = User.create("nickname", "tester", "password");
        DiaryCreateRequest request = new DiaryCreateRequest(10L, 1L, "내용", Emotion.HAPPY);
        MultipartFile image = org.mockito.Mockito.mock(MultipartFile.class);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findByUser(user)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memoryService.createDiary(request, image, "tester"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("유저가 메모리를 가지고 있지 않습니다.");
    }

    @DisplayName("일기를 성공적으로 생성한다")
    @Test
    void createDiary() {
        // given
        User user = User.create("nickname", "tester", "password");
        ReflectionTestUtils.setField(user, "id", 1L);

        Memory memory = Memory.create(user, "테스트 메모리");
        ReflectionTestUtils.setField(memory, "id", 10L);

        DiaryCreateRequest request = new DiaryCreateRequest(10L, 1L, "내용", Emotion.HAPPY);
        MultipartFile image = org.mockito.Mockito.mock(MultipartFile.class);
        DiaryCreateResponse expectedResponse = new DiaryCreateResponse(100L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(memoryRepository.findByUser(user)).willReturn(Optional.of(memory));
        given(diaryService.createDiary(request, image, user, memory)).willReturn(expectedResponse);

        // when
        DiaryCreateResponse result = memoryService.createDiary(request, image, "tester");

        // then
        assertThat(result.diaryId()).isEqualTo(100L);
        verify(diaryService).createDiary(request, image, user, memory);
    }

    private Diary createDiary(Long id, User user, Memory memory, Mission mission, String content, Emotion mood) {
        Diary diary = Diary.create(user, memory, mission, content, mood);
        ReflectionTestUtils.setField(diary, "id", id);
        ReflectionTestUtils.setField(diary, "createdAt", LocalDateTime.now());
        return diary;
    }

    private DiaryImage createDiaryImage(Long id, String s3Key, Diary diary) {
        DiaryImage image = DiaryImage.create(s3Key, 1024L, diary);
        ReflectionTestUtils.setField(image, "id", id);
        return image;
    }
}
