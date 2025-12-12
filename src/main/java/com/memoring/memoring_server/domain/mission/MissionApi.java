package com.memoring.memoring_server.domain.mission;

import com.memoring.memoring_server.domain.mission.dto.MissionOptionListResponse;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectRequest;
import com.memoring.memoring_server.domain.mission.dto.MissionSelectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "미션", description = "미션 조회 및 선택 API")
public interface MissionApi {

    @Operation(
            summary = "미션 목록 조회",
            description = "사용자가 선택할 수 있는 미션 목록을 조회합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<MissionOptionListResponse> getMissionOptions();

    @Operation(
            summary = "사용자 미션 조회",
            description = "사용자가 선택한 미션을 조회합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "선택한 미션이 없음")
    })
    ResponseEntity<MissionSelectResponse> getSelectedMission(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "미션 선택",
            description = "미션을 선택하여 사용자에게 할당합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "미션 선택 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 미션 선택"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<MissionSelectResponse> selectMission(
            @RequestBody MissionSelectRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "미션 취소",
            description = "사용자에게 할당된 미션을 취소합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "미션 취소 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "취소할 미션이 없음")
    })
    ResponseEntity<Void> cancelMission(@AuthenticationPrincipal UserDetails userDetails);
}