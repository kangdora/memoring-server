package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "퀴즈 결과 제출 요청")
public record QuizResultRequestDto(
        @Schema(description = "퀴즈 답안 맵. 키는 문제 번호입니다.")
        Map<Integer, QuizAnswerRequestDto> answers
) {
}
