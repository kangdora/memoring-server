package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.quiz.dto.QuizResultRequestDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponseDto;
import com.memoring.memoring_server.domain.quiz.dto.QuizSetResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController implements QuizApi {

    private final QuizService quizService;

    @Override
    @GetMapping
    public ResponseEntity<List<QuizSetResponseDto>> getQuizSets(@AuthenticationPrincipal UserDetails userDetails) {
        List<QuizSetResponseDto> response = quizService.getQuizSets(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{quizSetId}/results")
    public ResponseEntity<QuizResultResponseDto> submitQuizResult(
            @PathVariable Long quizSetId,
            @RequestBody QuizResultRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        QuizResultResponseDto response = quizService.createQuizResult(quizSetId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

}
