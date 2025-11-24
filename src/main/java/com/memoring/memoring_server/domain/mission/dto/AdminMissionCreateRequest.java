package com.memoring.memoring_server.domain.mission.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자용 미션 생성 요청")
public record AdminMissionCreateRequest(
        @Schema(description = "미션 내용", example = "오늘 있었던 일 중 가장 기억에 남는 것을 작성하세요")
        String content
) {
}
