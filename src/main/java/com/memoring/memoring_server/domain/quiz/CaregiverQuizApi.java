package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(
        name = "보호자 퀴즈",
        description = "보호자 전용 퀴즈 결과 조회 API"
)
public interface CaregiverQuizApi {

    @Operation(
            summary = "퀴즈 결과 조회",
            description = "연결된 환자의 퀴즈 결과를 조회합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퀴즈 결과 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "보호자 권한이 필요하거나 연결되지 않은 환자"),
            @ApiResponse(responseCode = "404", description = "퀴즈 결과를 찾을 수 없음")
    })
    ResponseEntity<QuizResultResponse> getQuizResult(
            @PathVariable Long quizResultId,
            @AuthenticationPrincipal UserDetails userDetails
    );
}
