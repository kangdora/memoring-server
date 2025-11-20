package com.memoring.memoring_server.domain.quiz.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "퀴즈 답안")
public record QuizAnswer(
        @Schema(description = "사용자의 답변", example = "정답 예시")
        @JsonProperty("user_answer")
        String userAnswer,

        @Schema(description = "정답 여부", example = "true")
        @JsonProperty("is_correct")
        Boolean isCorrect
) {
}
