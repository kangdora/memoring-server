package com.memoring.memoring_server.domain.quiz.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자용 퀴즈 아이템")
public record AdminQuizItemRequest(
        @Schema(description = "문제 본문", example = "사과를 영어로 무엇이라고 하나요?")
        String content,
        @Schema(description = "채점 보조 프롬프트", example = "정확한 영어 단어를 요구합니다")
        String prompt
) {
}
