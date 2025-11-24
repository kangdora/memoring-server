package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.quiz.dto.QuizResultRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponse;
import com.memoring.memoring_server.domain.quiz.dto.QuizSetResponse;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController implements QuizApi {

    private final QuizService quizService;

    @Override
    @GetMapping
    public ResponseEntity<List<QuizSetResponse>> getQuizSets(@AuthenticationPrincipal UserDetails userDetails) {
        List<QuizSetResponse> response = quizService.getQuizSets(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/{quizSetId}/results")
    public ResponseEntity<QuizResultResponse> submitQuizResult(
            @PathVariable Long quizSetId,
            @RequestBody QuizResultRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        QuizResultResponse response = quizService.createQuizResult(quizSetId, request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SttTranscriptionResponse> transcribeQuizAudio(
            @RequestPart("file") MultipartFile file
    ) {
        SttTranscriptionResponse response = quizService.transcribeQuizAudio(file);
        return ResponseEntity.ok(response);
    }
}
