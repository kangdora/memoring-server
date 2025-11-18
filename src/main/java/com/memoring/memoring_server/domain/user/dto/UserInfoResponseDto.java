package com.memoring.memoring_server.domain.user.dto;

import com.memoring.memoring_server.domain.user.User;

public record UserInfoResponseDto(
        String username,
        String nickname
) {
    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getUsername(),
                user.getNickname()
        );
    }
}
