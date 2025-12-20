package com.memoring.memoring_server.domain.caregiver.dto;

import com.memoring.memoring_server.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import com.memoring.memoring_server.domain.user.UserType;

@Schema(description = "환자 정보")
public record CaregiverPatientResponse(
        @Schema(description = "환자 ID")
        Long patientId,

        @Schema(description = "환자 닉네임")
        String nickname,

        @Schema(description = "유저 타입")
        UserType userType
) {
    public static CaregiverPatientResponse from(User user) {
        return new CaregiverPatientResponse(user.getId(), user.getNickname(), user.getUserType());
    }
}
