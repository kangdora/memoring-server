package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.caregiver.CareRelationService;
import com.memoring.memoring_server.domain.caregiver.dto.CaregiverMissionStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/caregiver/patients/{patientId}/mission")
@RequiredArgsConstructor
public class CaregiverMissionController implements CaregiverMissionApi {

    private final MissionService missionService;
    private final CareRelationService careRelationService;

    @Override
    @GetMapping
    public ResponseEntity<CaregiverMissionStatusResponse> getMissionStatus(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        careRelationService.validateCaregiverAccessToUser(userDetails.getUsername(), patientId);
        return ResponseEntity.ok(missionService.getMissionStatus(patientId));
    }
}