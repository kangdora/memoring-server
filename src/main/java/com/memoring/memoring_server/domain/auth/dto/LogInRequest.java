package com.memoring.memoring_server.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public record LogInRequest(
        @Schema(description = "회원 아이디")
        String username,

        @Schema(description = "회원 비밀번호")
        String password
) {}
