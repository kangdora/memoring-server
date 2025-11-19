package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "미션 선택 요청")
public record MissionSelectRequestDto(
        @Schema(description = "선택할 미션 ID")
        Long missionId
) {
}
