package com.memoring.memoring_server.domain.caregiver.dto;

import com.memoring.memoring_server.domain.user.Role;
import com.memoring.memoring_server.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "환자 정보")
public record CaregiverPatientResponse(
        @Schema(description = "환자 ID")
        Long patientId,

        @Schema(description = "환자 닉네임")
        String nickname,

        @Schema(description = "역할")
        Role role
) {
    public static CaregiverPatientResponse from(User user) {
        return new CaregiverPatientResponse(user.getId(), user.getNickname(), user.getRole());
    }
}
