package com.memoring.memoring_server.domain.memory.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "메모리 일기 요약")
public record MemoryDiarySummaryDto(
        @Schema(description = "일기 ID")
        Long diaryId,

        @Schema(description = "일기 작성 날짜")
        LocalDate date,

        @Schema(description = "일기 이미지 URL")
        String imageUrl,

        @Schema(description = "일기 내용")
        String content
) {
}
