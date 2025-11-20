package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자가 선택할 수 있는 미션 항목")
public record MissionOptionResponse(
        @Schema(description = "미션 ID")
        Long id,
        @Schema(description = "미션 내용")
        String content
) {
}
