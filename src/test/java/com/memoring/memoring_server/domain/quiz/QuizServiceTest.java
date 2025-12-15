package com.memoring.memoring_server.domain.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoring.memoring_server.domain.quiz.dto.AdminQuizCreateRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswer;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswerRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponse;
import com.memoring.memoring_server.domain.quiz.dto.QuizSetResponse;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizAlreadyTakenTodayException;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizAnswerRequiredException;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizSetLockedException;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.InvalidAdminRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuizServiceTest {

    @Mock
    private QuizSetRepository quizSetRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizResultRepository quizResultRepository;

    @Mock
    private UserService userService;

    @Mock
    private QuizGradingService quizGradingService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private QuizService quizService;

    @DisplayName("잠겨 있는 퀴즈 세트는 응시할 수 없다")
    @Test
    void createQuizResultFailsWhenQuizSetLocked() {
        User user = User.create("nickname", "tester", "password");
        QuizSet quizSet = QuizSet.create();
        ReflectionTestUtils.setField(quizSet, "id", 2L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(quizSetRepository.findById(2L)).willReturn(java.util.Optional.of(quizSet));
        given(quizSetRepository.countByIdLessThanEqual(2L)).willReturn(2L);

        QuizResultRequest request = new QuizResultRequest(Map.of());

        assertThatThrownBy(() -> quizService.createQuizResult(2L, request, "tester"))
                .isInstanceOf(QuizSetLockedException.class);
    }

    @DisplayName("같은 날 두 번 응시하면 예외가 발생한다")
    @Test
    void createQuizResultFailsWhenAlreadyTakenToday() {
        User user = User.create("nickname", "tester", "password");
        QuizSet quizSet = QuizSet.create();
        ReflectionTestUtils.setField(quizSet, "id", 1L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(quizSetRepository.findById(1L)).willReturn(java.util.Optional.of(quizSet));
        given(quizSetRepository.countByIdLessThanEqual(1L)).willReturn(1L);
        given(quizResultRepository.existsByUserAndQuizSetAndTakenAt(eq(user), eq(quizSet), any(LocalDate.class)))
                .willReturn(true);

        QuizResultRequest request = new QuizResultRequest(Map.of(1, new QuizAnswerRequest("answer")));

        assertThatThrownBy(() -> quizService.createQuizResult(1L, request, "tester"))
                .isInstanceOf(QuizAlreadyTakenTodayException.class);
    }

    @DisplayName("퀴즈 응시 시 채점 후 보상을 지급한다")
    @Test
    void createQuizResultRewardsAndGrades() {
        User user = User.create("nickname", "tester", "password");
        QuizSet quizSet = QuizSet.create();
        ReflectionTestUtils.setField(quizSet, "id", 1L);

        Quiz quiz1 = Quiz.create(quizSet, "content-1", "prompt-1");
        Quiz quiz2 = Quiz.create(quizSet, "content-2", "prompt-2");
        ReflectionTestUtils.setField(quiz1, "id", 10L);
        ReflectionTestUtils.setField(quiz2, "id", 11L);

        Map<Integer, QuizAnswerRequest> answers = new LinkedHashMap<>();
        answers.put(1, new QuizAnswerRequest("first"));
        answers.put(2, new QuizAnswerRequest("second"));

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(quizSetRepository.findById(1L)).willReturn(java.util.Optional.of(quizSet));
        given(quizSetRepository.countByIdLessThanEqual(1L)).willReturn(1L);
        given(quizResultRepository.existsByUserAndQuizSetAndTakenAt(eq(user), eq(quizSet), any(LocalDate.class)))
                .willReturn(false);
        given(quizRepository.findAllByQuizSetOrderByIdAsc(quizSet)).willReturn(List.of(quiz1, quiz2));
        given(quizGradingService.grade(eq(quiz1), eq("first")))
                .willReturn(new QuizAnswer("first", true));
        given(quizGradingService.grade(eq(quiz2), eq("second")))
                .willReturn(new QuizAnswer("second", false));

        QuizResultRequest request = new QuizResultRequest(answers);

        QuizResultResponse response = quizService.createQuizResult(1L, request, "tester");

        assertThat(response.quizSetId()).isEqualTo(1L);
        assertThat(response.answers()).containsEntry(1, new QuizAnswer("first", true));
        assertThat(response.answers()).containsEntry(2, new QuizAnswer("second", false));
        assertThat(response.answerCount()).isEqualTo(1);
        assertThat(user.getCoin()).isEqualTo(10L);
        assertThat(user.getQuizProgress()).isEqualTo(1);

        verify(quizResultRepository).save(any(QuizResult.class));
    }

    @DisplayName("퀴즈 응답이 없으면 예외가 발생한다")
    @Test
    void createQuizResultFailsWhenAnswersMissing() {
        User user = User.create("nickname", "tester", "password");
        QuizSet quizSet = QuizSet.create();
        ReflectionTestUtils.setField(quizSet, "id", 1L);

        Quiz quiz = Quiz.create(quizSet, "content-1", "prompt-1");
        ReflectionTestUtils.setField(quiz, "id", 10L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(quizSetRepository.findById(1L)).willReturn(java.util.Optional.of(quizSet));
        given(quizSetRepository.countByIdLessThanEqual(1L)).willReturn(1L);
        given(quizResultRepository.existsByUserAndQuizSetAndTakenAt(eq(user), eq(quizSet), any(LocalDate.class)))
                .willReturn(false);
        given(quizRepository.findAllByQuizSetOrderByIdAsc(quizSet)).willReturn(List.of(quiz));

        QuizResultRequest request = new QuizResultRequest(Map.of());

        assertThatThrownBy(() -> quizService.createQuizResult(1L, request, "tester"))
                .isInstanceOf(QuizAnswerRequiredException.class);
    }

    @DisplayName("퀴즈 세트 생성 시 잘못된 요청이면 예외가 발생한다")
    @Test
    void createQuizSetFailsWhenInvalid() {
        assertThatThrownBy(() -> quizService.createQuizSet(new AdminQuizCreateRequest(List.of())))
                .isInstanceOf(InvalidAdminRequestException.class);
    }

    @DisplayName("사용자 진행도에 따라 퀴즈 세트 잠금 여부를 반환한다")
    @Test
    void getQuizSets() {
        User user = User.create("nick", "tester", "pass");
        ReflectionTestUtils.setField(user, "quizProgress", 1);

        QuizSet set1 = QuizSet.create();
        QuizSet set2 = QuizSet.create();
        ReflectionTestUtils.setField(set1, "id", 1L);
        ReflectionTestUtils.setField(set2, "id", 2L);

        Quiz quiz1 = Quiz.create(set1, "q1", "p1");
        Quiz quiz2 = Quiz.create(set2, "q2", "p2");
        ReflectionTestUtils.setField(quiz1, "id", 10L);
        ReflectionTestUtils.setField(quiz2, "id", 20L);

        given(userService.getUserByUsername("tester")).willReturn(user);
        given(quizSetRepository.findAllByOrderByIdAsc()).willReturn(List.of(set1, set2));
        given(quizRepository.findAllByQuizSetInOrderByQuizSetIdAscIdAsc(List.of(set1, set2)))
                .willReturn(List.of(quiz1, quiz2));

        List<QuizSetResponse> responses = quizService.getQuizSets("tester");

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).unlocked()).isTrue();
        assertThat(responses.get(1).unlocked()).isTrue(); // quizProgress=1 unlocks second set
        assertThat(responses.get(0).quizzes()).hasSize(1);
    }
}
