package com.memoring.memoring_server.domain.user.dto;

public record UserLoginResponseDto(
        String accessToken,
        String refreshToken,
        String tokenType,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {}
