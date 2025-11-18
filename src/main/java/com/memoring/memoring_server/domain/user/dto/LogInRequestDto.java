package com.memoring.memoring_server.domain.user.dto;

public record LogInRequestDto(
        String username,
        String password
) {}
