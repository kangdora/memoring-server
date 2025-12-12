package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자가 선택할 수 있는 미션 항목을 감싸는 리스트")
public record MissionOptionListResponse(
        List<MissionOptionResponse> missions
) {
}
