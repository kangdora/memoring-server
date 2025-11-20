package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "선택된 미션 응답")
public record MissionSelectResponse(
        @Schema(description = "사용자에게 할당된 미션 ID")
        Long userMissionId,
        @Schema(description = "미션 내용")
        String content
) {
}
