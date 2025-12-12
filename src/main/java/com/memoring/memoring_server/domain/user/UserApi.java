package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.SignUpRequest;
import com.memoring.memoring_server.domain.user.dto.UserSignUpResponse;
import com.memoring.memoring_server.domain.user.dto.UserInfoResponse;
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
        name = "사용자",
        description = "사용자 정보 관련 API"
)
public interface UserApi {

    @Operation(
            summary = "현재 사용자 정보 조회",
            description = "인증된 사용자의 아이디(username)와 닉네임(nickname)을 조회합니다.",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 사용자 정보 조회 성공")
    })
    ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "회원가입",
            description = "새로운 사용자 계정을 생성하고 JWT 토큰을 발급합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공 및 사용자 생성 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 회원가입 요청 데이터")
    })
    ResponseEntity<UserSignUpResponse> signup(@RequestBody SignUpRequest request);
}