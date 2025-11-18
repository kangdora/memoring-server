package com.memoring.memoring_server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginResponseDto(
        @Schema(description = "발급된 액세스 토큰")
        String accessToken,

        @Schema(description = "발급된 리프레시 토큰")
        String refreshToken,

        @Schema(description = "토큰 타입")
        String tokenType,

        @Schema(description = "액세스 토큰 만료 시간(밀리초)")
        long accessTokenExpiresIn,

        @Schema(description = "리프레시 토큰 만료 시간(밀리초)")
        long refreshTokenExpiresIn
) {}
