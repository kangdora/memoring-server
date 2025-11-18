package com.memoring.memoring_server.domain.user.dto;

public record UserSignUpResponseDto(
        String message,
        String accessToken,
        String refreshToken,
        String tokenType,
        String username,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {}
