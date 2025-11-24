package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "선택된 미션 응답")
public record MissionSelectResponse(
        @Schema(description = "선택된 미션 ID")
        Long missionId,
        @Schema(description = "미션 내용")
        String content
) {
}
