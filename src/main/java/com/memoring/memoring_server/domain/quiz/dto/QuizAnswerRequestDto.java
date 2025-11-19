package com.memoring.memoring_server.domain.quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "퀴즈 답안 제출")
public record QuizAnswerRequestDto(
        @Schema(description = "사용자의 답변", example = "사용자 답변 예시")
        @JsonProperty("user_answer")
        String userAnswer
) {
}
