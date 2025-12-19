package com.memoring.memoring_server.domain.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memoring.memoring_server.domain.caregiver.CareRelationService;
import com.memoring.memoring_server.domain.caregiver.exception.CareRelationAccessDeniedException;
import com.memoring.memoring_server.domain.quiz.dto.*;
import com.memoring.memoring_server.domain.quiz.excpetion.*;
import com.memoring.memoring_server.domain.user.Role;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.exception.*;
import com.memoring.memoring_server.global.external.openai.stt.SttService;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private static final TypeReference<LinkedHashMap<Integer, QuizAnswer>> ANSWER_MAP_TYPE = new TypeReference<>() {};
    private final QuizSetRepository quizSetRepository;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final QuizGradingService quizGradingService;
    private final SttService sttService;
    private final CareRelationService careRelationService;

    @Transactional
    public AdminQuizSetResponse createQuizSet(AdminQuizCreateRequest request) {
        if (request == null || CollectionUtils.isEmpty(request.quizzes())) {
            throw new InvalidAdminRequestException();
        }

        QuizSet quizSet = quizSetRepository.save(QuizSet.create());

        List<Quiz> quizzes = request.quizzes().stream()
                .map(this::validateAndCreateQuiz)
                .map(item -> Quiz.create(quizSet, item.content(), item.prompt()))
                .toList();

        List<Long> quizIds = quizRepository.saveAll(quizzes).stream()
                .map(Quiz::getId)
                .toList();

        return new AdminQuizSetResponse(quizSet.getId(), quizIds);
    }

    public List<QuizSetResponse> getQuizSets(String username) {
        User user = userService.getUserByUsername(username);
        List<QuizSet> quizSets = quizSetRepository.findAllByOrderByIdAsc();
        if (quizSets.isEmpty()) {
            return List.of();
        }

        List<Quiz> quizzes = quizRepository.findAllByQuizSetInOrderByQuizSetIdAscIdAsc(quizSets);
        Map<Long, List<QuizItemResponse>> quizzesBySet = quizzes.stream()
                .collect(Collectors.groupingBy(
                        quiz -> quiz.getQuizSet().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(quiz -> new QuizItemResponse(
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
                    List<QuizItemResponse> quizItems = quizzesBySet.getOrDefault(set.getId(), List.of());
                    return new QuizSetResponse(set.getId(), seq, unlocked, quizItems);
                })
                .toList();
    }

    @Transactional
    public QuizResultResponse createQuizResult(Long quizSetId, QuizResultRequest request, String username) {
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

        Map<Integer, QuizAnswerRequest> normalizedAnswers = normalizeAnswers(request == null ? null : request.answers());
        List<Quiz> quizzes = quizRepository.findAllByQuizSetOrderByIdAsc(quizSet);
        Map<Integer, QuizAnswer> gradedAnswers = gradeAnswers(quizzes, normalizedAnswers);
        int correctCount = countCorrectAnswers(gradedAnswers);
        String answerJson = writeAnswers(gradedAnswers);

        QuizResult quizResult = QuizResult.create(user, quizSet, today, answerJson, correctCount);
        quizResultRepository.save(quizResult);

        user.addCoin(QUIZ_REWARD_COINS);
        user.addQuizProgress();

        return toQuizResultResponse(quizResult, gradedAnswers);
    }

    public SttTranscriptionResponse transcribeQuizAudio(MultipartFile file) {
        return sttService.transcribe(file);
    }

    public QuizResultResponse getQuizResult(Long quizResultId, String username) {
        User caregiver = userService.getUserByUsername(username);
        if (!Role.CAREGIVER.equals(caregiver.getRole())) {
            throw new CareRelationAccessDeniedException();
        }

        QuizResult quizResult = quizResultRepository.findById(quizResultId)
                .orElseThrow(QuizResultNotFoundException::new);

        Long patientId = quizResult.getUser().getId();
        if (!careRelationService.isConnected(patientId, caregiver.getId())) {
            throw new CareRelationAccessDeniedException();
        }

        Map<Integer, QuizAnswer> answers = readAnswers(quizResult.getAnswer());

        return new QuizResultResponse(
                quizResult.getId(),
                quizResult.getQuizSet().getId(),
                quizResult.getTakenAt(),
                Collections.unmodifiableMap(new LinkedHashMap<>(answers)),
                quizResult.getAnswerCount()
        );
    }

    private Map<Integer, QuizAnswerRequest> normalizeAnswers(Map<Integer, QuizAnswerRequest> answers) {
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

    private Map<Integer, QuizAnswer> gradeAnswers(List<Quiz> quizzes, Map<Integer, QuizAnswerRequest> answers) {
        if (quizzes.isEmpty()) {
            throw new QuizSetNotFoundException();
        }

        if (answers.size() != quizzes.size()) {
            throw new QuizAnswerRequiredException();
        }

        Map<Integer, QuizAnswer> graded = new LinkedHashMap<>();
        for (int index = 0; index < quizzes.size(); index++) {
            int questionNumber = index + 1;
            QuizAnswerRequest answerRequest = answers.get(questionNumber);
            if (answerRequest == null) {
                throw new QuizAnswerRequiredException();
            }
            if (answerRequest.userAnswer() == null || answerRequest.userAnswer().isBlank()) {
                throw new QuizAnswerRequiredException();
            }
            Quiz quiz = quizzes.get(index);
            QuizAnswer evaluated = quizGradingService.grade(quiz, answerRequest.userAnswer());
            graded.put(questionNumber, evaluated);
        }
        return graded;
    }

    private int countCorrectAnswers(Map<Integer, QuizAnswer> answers) {
        return (int) answers.values().stream()
                .filter(answer -> Boolean.TRUE.equals(answer.isCorrect()))
                .count();
    }

    private AdminQuizItemRequest validateAndCreateQuiz(AdminQuizItemRequest request) {
        if (request == null || !StringUtils.hasText(request.content())) {
            throw new InvalidAdminRequestException();
        }
        String prompt = request.prompt();
        if (prompt == null) {
            prompt = "";
        }
        return new AdminQuizItemRequest(request.content(), prompt);
    }

    private QuizResultResponse toQuizResultResponse(QuizResult quizResult, Map<Integer, QuizAnswer> answers) {
        return new QuizResultResponse(
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

    private String writeAnswers(Map<Integer, QuizAnswer> answers) {
        try {
            return objectMapper.writeValueAsString(answers);
        } catch (JsonProcessingException e) {
            throw new QuizAnswerSerializationException(e);
        }
    }

    private Map<Integer, QuizAnswer> readAnswers(String answers) {
        try {
            return objectMapper.readValue(answers, ANSWER_MAP_TYPE);
        } catch (JsonProcessingException e) {
            throw new QuizAnswerSerializationException(e);
        }
    }
}
