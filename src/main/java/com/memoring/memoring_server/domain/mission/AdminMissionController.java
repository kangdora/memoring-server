package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.AdminMissionCreateRequest;
import com.memoring.memoring_server.domain.mission.dto.AdminMissionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/missions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 미션", description = "관리자용 미션 관리 API")
public class AdminMissionController {

    private final MissionService missionService;

    @Operation(summary = "미션 생성", description = "관리자가 새로운 미션을 생성합니다.")
    @PostMapping
    public ResponseEntity<AdminMissionResponse> createMission(@RequestBody AdminMissionCreateRequest request) {
        AdminMissionResponse response = missionService.createMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
