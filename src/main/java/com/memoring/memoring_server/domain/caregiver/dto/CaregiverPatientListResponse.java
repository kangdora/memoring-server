package com.memoring.memoring_server.domain.caregiver.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "환자 정보들")
public record CaregiverPatientListResponse(
        @Schema(description = "환자 정보들")
        List<CaregiverPatientResponse> patients
) {}
