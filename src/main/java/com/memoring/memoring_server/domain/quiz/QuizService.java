package com.memoring.memoring_server.domain.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswerDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizAnswerRequestDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizItemResponseDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultRequestDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponseDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizSetResponseDto;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.QuizAlreadyTakenTodayException;
import com.memoring.memoring_server.global.exception.QuizAnswerRequiredException;
import com.memoring.memoring_server.global.exception.QuizSetLockedException;
import com.memoring.memoring_server.global.exception.QuizSetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    private static final Long QUIZ_REWARD_COINS = 10L;
    private final QuizSetRepository quizSetRepository;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final QuizGradingService quizGradingService;

    public List<QuizSetResponseDto> getQuizSets(String username) {
        User user = userService.getUserByUsername(username);
        List<QuizSet> quizSets = quizSetRepository.findAllByOrderByIdAsc();
        if (quizSets.isEmpty()) {
            return List.of();
        }

        List<Quiz> quizzes = quizRepository.findAllByQuizSetInOrderByQuizSetIdAscIdAsc(quizSets);
        Map<Long, List<QuizItemResponseDto>> quizzesBySet = quizzes.stream()
                .collect(Collectors.groupingBy(
                        quiz -> quiz.getQuizSet().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(quiz -> new QuizItemResponseDto(
                                quiz.getId(),
                                quiz.getContent()
                        ), Collectors.toList())
                ));

        AtomicInteger sequence = new AtomicInteger(1);
        int unlockedThreshold = user.getQuizProgress() + 1;

        return quizSets.stream()
                .map(set -> {
                    int seq = sequence.getAndIncrement();
                    boolean unlocked = seq <= unlockedThreshold;
                    List<QuizItemResponseDto> quizItems = quizzesBySet.getOrDefault(set.getId(), List.of());
                    return new QuizSetResponseDto(set.getId(), seq, unlocked, quizItems);
                })
                .toList();
    }

    @Transactional
    public QuizResultResponseDto createQuizResult(Long quizSetId, QuizResultRequestDto request, String username) {
        User user = userService.getUserByUsername(username);
        QuizSet quizSet = quizSetRepository.findById(quizSetId)
                .orElseThrow(QuizSetNotFoundException::new);

        int quizSetSequence = getQuizSetSequence(quizSet);
        int expectedSequence = user.getQuizProgress() + 1;
        if (quizSetSequence != expectedSequence) {
            throw new QuizSetLockedException();
        }

        LocalDate today = LocalDate.now();
        if (quizResultRepository.existsByUserAndQuizSetAndTakenAt(user, quizSet, today)) {
            throw new QuizAlreadyTakenTodayException();
        }

        Map<Integer, QuizAnswerRequestDto> normalizedAnswers = normalizeAnswers(request == null ? null : request.answers());
        List<Quiz> quizzes = quizRepository.findAllByQuizSetOrderByIdAsc(quizSet);
        Map<Integer, QuizAnswerDto> gradedAnswers = gradeAnswers(quizzes, normalizedAnswers);
        int correctCount = countCorrectAnswers(gradedAnswers);
        String answerJson = writeAnswers(gradedAnswers);

        QuizResult quizResult = QuizResult.create(user, quizSet, today, answerJson, correctCount);
        quizResultRepository.save(quizResult);

        user.addCoin(QUIZ_REWARD_COINS);
        user.addQuizProgress();

        return toQuizResultResponse(quizResult, gradedAnswers);
    }

    private Map<Integer, QuizAnswerRequestDto> normalizeAnswers(Map<Integer, QuizAnswerRequestDto> answers) {
        if (answers == null || answers.isEmpty()) {
            throw new QuizAnswerRequiredException();
        }

        return answers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (left, right) -> right,
                        LinkedHashMap::new
                ));
    }

    private Map<Integer, QuizAnswerDto> gradeAnswers(List<Quiz> quizzes, Map<Integer, QuizAnswerRequestDto> answers) {
        if (quizzes.isEmpty()) {
            throw new QuizSetNotFoundException();
        }

        if (answers.size() != quizzes.size()) {
            throw new QuizAnswerRequiredException();
        }

        Map<Integer, QuizAnswerDto> graded = new LinkedHashMap<>();
        for (int index = 0; index < quizzes.size(); index++) {
            int questionNumber = index + 1;
            QuizAnswerRequestDto answerRequest = answers.get(questionNumber);
            if (answerRequest == null) {
                throw new QuizAnswerRequiredException();
            }
            if (answerRequest.userAnswer() == null || answerRequest.userAnswer().isBlank()) {
                throw new QuizAnswerRequiredException();
            }
            Quiz quiz = quizzes.get(index);
            QuizAnswerDto evaluated = quizGradingService.grade(quiz, answerRequest.userAnswer());
            graded.put(questionNumber, evaluated);
        }
        return graded;
    }

    private int countCorrectAnswers(Map<Integer, QuizAnswerDto> answers) {
        return (int) answers.values().stream()
                .filter(answer -> Boolean.TRUE.equals(answer.isCorrect()))
                .count();
    }

    private QuizResultResponseDto toQuizResultResponse(QuizResult quizResult, Map<Integer, QuizAnswerDto> answers) {
        return new QuizResultResponseDto(
                quizResult.getId(),
                quizResult.getQuizSet().getId(),
                quizResult.getTakenAt(),
                Collections.unmodifiableMap(new LinkedHashMap<>(answers)),
                quizResult.getAnswerCount()
        );
    }

    private int getQuizSetSequence(QuizSet quizSet) {
        long sequence = quizSetRepository.countByIdLessThanEqual(quizSet.getId());
        if (sequence <= 0) {
            throw new QuizSetNotFoundException();
        }
        return Math.toIntExact(sequence);
    }

    private String writeAnswers(Map<Integer, QuizAnswerDto> answers) {
        try {
            return objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("퀴즈 답안을 저장하는 중 오류가 발생했습니다.", e);
        }
    }
}
