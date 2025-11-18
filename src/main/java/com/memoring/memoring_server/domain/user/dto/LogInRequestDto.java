package com.memoring.memoring_server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LogInRequestDto(
        @Schema(description = "회원 아이디")
        String username,

        @Schema(description = "회원 비밀번호")
        String password
) {}
