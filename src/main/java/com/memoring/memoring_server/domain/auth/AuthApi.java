package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.dto.LogInRequest;
import com.memoring.memoring_server.domain.auth.dto.TokenRefreshRequest;
import com.memoring.memoring_server.domain.auth.dto.UserLoginResponse;
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
        name = "인증",
        description = "사용자 인증 관련 API (로그인, 로그아웃, 토큰 재발급, 회원가입)"
)
public interface AuthApi {

    @Operation(
            summary = "로그인",
            description = "사용자 로그인 요청을 처리하고, 성공 시 JWT 액세스 토큰과 리프레시 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 올바르지 않음")
    })
    ResponseEntity<UserLoginResponse> login(@RequestBody LogInRequest request);

    @Operation(
            summary = "로그아웃",
            description = "현재 인증된 사용자의 리프레시 토큰을 무효화합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "액세스 토큰 재발급",
            description = "유효한 리프레시 토큰을 사용하여 새로운 액세스 토큰(및 필요 시 리프레시 토큰)을 재발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰이 만료되었거나 유효하지 않음")
    })
    ResponseEntity<UserLoginResponse> refresh(@RequestBody TokenRefreshRequest request);
}
