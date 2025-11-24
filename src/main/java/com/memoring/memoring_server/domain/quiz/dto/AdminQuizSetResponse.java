package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "관리자용 퀴즈 세트 생성 응답")
public record AdminQuizSetResponse(
        @Schema(description = "퀴즈 세트 ID")
        Long quizSetId,
        @Schema(description = "생성된 퀴즈 ID 목록")
        List<Long> quizIds
) {
}
