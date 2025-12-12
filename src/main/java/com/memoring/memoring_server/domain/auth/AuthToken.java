package com.memoring.memoring_server.domain.auth;

public record AuthToken(
        String accessToken,
        String refreshToken,
        String tokenType,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {}
