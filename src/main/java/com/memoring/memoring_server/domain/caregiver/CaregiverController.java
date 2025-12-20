package com.memoring.memoring_server.domain.caregiver;

import com.memoring.memoring_server.domain.caregiver.dto.CareInviteAcceptRequest;
import com.memoring.memoring_server.domain.caregiver.dto.CaregiverPatientListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/caregiver")
@RequiredArgsConstructor
public class CaregiverController implements CaregiverApi {

    private final CareRelationService careRelationService;

    @Override
    @PostMapping("/care/invite/accept")
    public ResponseEntity<Void> acceptInvite(
            @RequestBody CareInviteAcceptRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        careRelationService.acceptCareInvite(request, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/patients")
    public ResponseEntity<CaregiverPatientListResponse> getPatients(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(careRelationService.getPatients(userDetails.getUsername()));
    }
}
