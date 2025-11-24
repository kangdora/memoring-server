package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.quiz.dto.AdminQuizCreateRequest;
import com.memoring.memoring_server.domain.quiz.dto.AdminQuizSetResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/quizzes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 퀴즈", description = "관리자용 퀴즈 관리 API")
public class AdminQuizController {

    private final QuizService quizService;

    @Operation(summary = "퀴즈 세트 생성", description = "관리자가 새로운 퀴즈 세트를 생성합니다.")
    @PostMapping
    public ResponseEntity<AdminQuizSetResponse> createQuizSet(@RequestBody AdminQuizCreateRequest request) {
        AdminQuizSetResponse response = quizService.createQuizSet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
