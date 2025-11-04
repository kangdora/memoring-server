package com.memoring.memoring_server.domain.user.dto;

public record LogInRequestDto(
        String id,
        String password
) {
}
