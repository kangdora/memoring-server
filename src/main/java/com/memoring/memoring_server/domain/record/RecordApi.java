package com.memoring.memoring_server.domain.record;

import com.memoring.memoring_server.domain.record.dto.RecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "녹음", description = "음성 녹음 업로드 및 재생 API")
public interface RecordApi {

    @Operation(
            summary = "녹음 업로드",
            description = "사용자가 선택한 미션에 대한 녹음을 업로드하고 저장합니다. STT는 수행하지 않습니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "녹음 업로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "선택된 미션이 없음")
    })
    ResponseEntity<RecordResponse> uploadRecord(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "녹음 재생 URL 조회",
            description = "저장된 녹음 파일을 재생할 수 있는 presigned URL을 반환합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "녹음 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "녹음이 존재하지 않음")
    })
    ResponseEntity<RecordResponse> getRecord(@AuthenticationPrincipal UserDetails userDetails);
}