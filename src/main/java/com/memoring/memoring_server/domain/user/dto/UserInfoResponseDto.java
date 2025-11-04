package com.memoring.memoring_server.domain.user.dto;

import com.memoring.memoring_server.domain.user.User;

public record UserInfoResponseDto(
        String loginId,
        String username,
        String role
) {
    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getLoginId(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}