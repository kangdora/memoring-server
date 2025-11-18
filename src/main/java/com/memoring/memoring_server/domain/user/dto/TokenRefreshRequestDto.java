package com.memoring.memoring_server.domain.user.dto;

public record TokenRefreshRequestDto(
        String refreshToken
) {}
