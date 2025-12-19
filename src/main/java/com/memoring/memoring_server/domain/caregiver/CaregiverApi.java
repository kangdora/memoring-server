package com.memoring.memoring_server.domain.caregiver;

import com.memoring.memoring_server.domain.caregiver.dto.CareInviteAcceptRequest;
import com.memoring.memoring_server.domain.caregiver.dto.CaregiverPatientListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
        name = "케어기버",
        description = "케어기버 전용 API"
)
public interface CaregiverApi {

    @Operation(
            summary = "케어 초대 수락",
            description = "환자가 발급한 초대 코드를 통해 케어 관계를 수락합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "케어 관계 수락 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 초대 코드"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "케어기버 권한이 필요함")
    })
    ResponseEntity<Void> acceptInvite(
            @RequestBody CareInviteAcceptRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "케어 대상자 목록",
            description = "케어기버와 연결된 환자 목록을 반환합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환자 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "케어기버 권한이 필요함")
    })
    ResponseEntity<CaregiverPatientListResponse> getPatients(
            @AuthenticationPrincipal UserDetails userDetails
    );
}
