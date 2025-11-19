package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Map;

@Schema(description = "퀴즈 결과 응답")
public record QuizResultResponseDto(
        @Schema(description = "퀴즈 결과 ID")
        Long quizResultId,

        @Schema(description = "퀴즈 세트 ID")
        Long quizSetId,

        @Schema(description = "퀴즈를 푼 날짜")
        LocalDate takenAt,

        @Schema(description = "사용자의 답안")
        Map<Integer, QuizAnswerDto> answers,

        @Schema(description = "정답 개수")
        Integer answerCount
) {
}
