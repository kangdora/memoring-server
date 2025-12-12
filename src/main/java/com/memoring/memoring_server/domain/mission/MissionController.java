package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.MissionOptionListResponse;
import com.memoring.memoring_server.domain.mission.dto.MissionOptionResponse;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectRequest;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mission")
@RequiredArgsConstructor
public class MissionController implements MissionApi {

    private final MissionService missionService;

    @Override
    @GetMapping("/selected")
    public ResponseEntity<MissionSelectResponse> getSelectedMission(@AuthenticationPrincipal UserDetails userDetails) {
        MissionSelectResponse response = missionService.getSelectedMission(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping
    public ResponseEntity<MissionOptionListResponse> getMissionOptions() {
        MissionOptionListResponse response = missionService.getMissionOptions();
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<MissionSelectResponse> selectMission(
            @RequestBody MissionSelectRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        MissionSelectResponse response = missionService.selectMission(request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> cancelMission(@AuthenticationPrincipal UserDetails userDetails) {
        missionService.cancelMission(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}