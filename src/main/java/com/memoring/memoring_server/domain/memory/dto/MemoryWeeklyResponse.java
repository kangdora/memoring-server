package com.memoring.memoring_server.domain.memory.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "메모리 주차별 조회 응답")
public record MemoryWeeklyResponse(
        @Schema(description = "주차 시작일 (월요일)")
        LocalDate weekStartDate,

        @Schema(description = "주차 종료일 (일요일)")
        LocalDate weekEndDate,

        @Schema(description = "주차 내 일기 첫번째 이미지 최대 3개")
        List<String> thumbnails,

        @Schema(description = "주차 내 일기 목록")
        List<MemoryDiaryResponse> diaries
) {
}