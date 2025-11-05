package com.memoring.memoring_server.domain.user.dto;

import com.memoring.memoring_server.domain.user.Role;

public record UserSignUpResponseDto(
        String message,
        String accessToken,
        String refreshToken,
        String tokenType,
        Role role,
        String username,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {
}