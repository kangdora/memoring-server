package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/caregiver/quizzes")
@RequiredArgsConstructor
public class CaregiverQuizController implements CaregiverQuizApi {

    private final QuizService quizService;

    @Override
    @GetMapping("/results/{quizResultId}")
    public ResponseEntity<QuizResultResponse> getQuizResult(
            @PathVariable Long quizResultId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(quizService.getQuizResult(quizResultId, userDetails.getUsername()));
    }
}
