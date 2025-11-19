package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "퀴즈 문제")
public record QuizItemResponseDto(
        @Schema(description = "퀴즈 ID")
        Long quizId,

        @Schema(description = "퀴즈 내용")
        String content
) {
}
