package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.MissionOptionResponseDto;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectRequestDto;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectResponseDto;
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
    @GetMapping
    public ResponseEntity<List<MissionOptionResponseDto>> getMissionOptions() {
        List<MissionOptionResponseDto> response = missionService.getMissionOptions();
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    public ResponseEntity<MissionSelectResponseDto> selectMission(
            @RequestBody MissionSelectRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        MissionSelectResponseDto response = missionService.selectMission(dto, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> cancelMission(@AuthenticationPrincipal UserDetails userDetails) {
        missionService.cancelMission(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}