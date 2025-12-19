package com.memoring.memoring_server.domain.caregiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "보호자 초대 수락 요청")
public record CareInviteAcceptRequest(
        @Schema(description = "초대 코드", example = "A9F3KD")
        String inviteCode
) {
}