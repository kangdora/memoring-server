package com.memoring.memoring_server.domain.user.dto;

import com.memoring.memoring_server.domain.user.Address;
import com.memoring.memoring_server.domain.user.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record SignUpRequest(

        @Schema(description = "사용자 닉네임")
        String nickname,

        @Schema(description = "로그인에 사용할 아이디")
        String username,

        @Schema(description = "로그인 비밀번호")
        String password,

        @Schema(description = "비밀번호 확인")
        String passwordConfirm,

        @Schema(description = "역할")
        Role role,

        @Schema(description = "주소")
        Address address
) {}
