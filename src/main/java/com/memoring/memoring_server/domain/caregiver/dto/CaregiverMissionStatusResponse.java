package com.memoring.memoring_server.domain.caregiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보호자가 조회하는 미션 진행 상태")
public record CaregiverMissionStatusResponse(
        @Schema(description = "미션 ID")
        Long missionId,

        @Schema(description = "미션 내용")
        String content,

        @Schema(description = "진행 완료 여부")
        boolean completed
) {
}
