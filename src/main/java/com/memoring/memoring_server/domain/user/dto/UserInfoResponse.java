package com.memoring.memoring_server.domain.user.dto;

import com.memoring.memoring_server.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 정보 응답")
public record UserInfoResponse(
        @Schema(description = "회원 아이디")
        String username,

        @Schema(description = "회원 닉네임")
        String nickname
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getUsername(),
                user.getNickname()
        );
    }
}
