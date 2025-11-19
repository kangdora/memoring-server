package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "퀴즈 세트 정보")
public record QuizSetResponseDto(
        @Schema(description = "퀴즈 세트 ID")
        Long quizSetId,

        @Schema(description = "세트 진행 순서")
        int sequence,

        @Schema(description = "세트 잠금 해제 여부")
        boolean unlocked,

        @Schema(description = "세트에 포함된 퀴즈")
        List<QuizItemResponseDto> quizzes
) {
}
