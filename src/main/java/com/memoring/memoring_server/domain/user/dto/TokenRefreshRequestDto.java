package com.memoring.memoring_server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenRefreshRequestDto(
        @Schema(description = "재발급에 사용할 리프레시 토큰")
        String refreshToken
) {}
