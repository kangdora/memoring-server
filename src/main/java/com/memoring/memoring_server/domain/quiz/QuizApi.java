package com.memoring.memoring_server.domain.quiz;

import com.memoring.memoring_server.domain.quiz.dto.QuizResultRequest;
import com.memoring.memoring_server.domain.quiz.dto.QuizResultResponse;
import com.memoring.memoring_server.domain.quiz.dto.QuizSetResponse;
import com.memoring.memoring_server.global.external.openai.stt.dto.SttTranscriptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "퀴즈", description = "퀴즈 조회 및 결과 검증 API")
public interface QuizApi {

    @Operation(
            summary = "퀴즈 세트 조회",
            description = "사용자의 진행 상황에 따라 열려 있는 퀴즈 세트를 조회합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퀴즈 세트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<List<QuizSetResponse>> getQuizSets(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "퀴즈 결과 저장",
            description = "퀴즈 세트를 풀고 생성된 결과를 저장합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "퀴즈 결과 저장 성공"),
            @ApiResponse(responseCode = "400", description = "요청 값이 올바르지 않거나 오늘 이미 퀴즈를 풂"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "아직 열리지 않은 퀴즈 세트"),
            @ApiResponse(responseCode = "404", description = "퀴즈 세트를 찾을 수 없음")
    })
    ResponseEntity<QuizResultResponse> submitQuizResult(
            @PathVariable Long quizSetId,
            @RequestBody QuizResultRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "퀴즈 음성 텍스트 변환",
            description = "퀴즈 응답 녹음 파일을 텍스트로 변환합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "변환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 요청"),
            @ApiResponse(responseCode = "500", description = "OpenAI 연동 오류")
    })
    ResponseEntity<SttTranscriptionResponse> transcribeQuizAudio(MultipartFile file);

}
