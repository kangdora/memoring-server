package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자용 미션 응답")
public record AdminMissionResponse(
        @Schema(description = "미션 ID")
        Long id,
        @Schema(description = "미션 내용")
        String content
) {
}
