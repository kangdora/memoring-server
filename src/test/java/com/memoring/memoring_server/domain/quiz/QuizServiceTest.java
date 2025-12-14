package com.memoring.memoring_server.domain.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswer;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswerRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponse;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizAlreadyTakenTodayException;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizAnswerRequiredException;
import com.memoring.memoring_server.domain.quiz.excpetion.QuizSetLockedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @DisplayName("퀴즈 세트 순서가 잠겨있으면 응시할 수 없다")
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

    @DisplayName("동일한 날에 이미 응시했다면 다시 풀 수 없다")
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

    @DisplayName("퀴즈 응시가 성공하면 채점 후 보상과 진행도가 갱신된다")
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

    @DisplayName("퀴즈 답안이 누락되면 예외가 발생한다")
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
}
