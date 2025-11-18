package com.memoring.memoring_server.domain.user.dto;

public record SignUpRequestDto(
        String nickname,
        String username,
        String password,
        String passwordConfirm
) {}
