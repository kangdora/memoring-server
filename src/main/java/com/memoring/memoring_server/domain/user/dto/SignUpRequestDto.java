package com.memoring.memoring_server.domain.user.dto;

public record SignUpRequestDto(
        String username,
        String id,
        String password,
        String passwordConfirm
) {
}
