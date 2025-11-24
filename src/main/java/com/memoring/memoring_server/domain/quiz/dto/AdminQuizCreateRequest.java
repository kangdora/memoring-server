package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "관리자용 퀴즈 세트 생성 요청")
public record AdminQuizCreateRequest(
        @Schema(description = "퀴즈 목록")
        List<AdminQuizItemRequest> quizzes
) {
}
