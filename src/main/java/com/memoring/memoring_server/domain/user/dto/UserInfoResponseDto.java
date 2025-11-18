package com.memoring.memoring_server.domain.user.dto;

import com.memoring.memoring_server.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoResponseDto(
        @Schema(description = "회원 아이디")
        String username,

        @Schema(description = "회원 닉네임")
        String nickname
) {
    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getUsername(),
                user.getNickname()
        );
    }
}
