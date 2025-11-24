package com.memoring.memoring_server.global.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("code", "UNAUTHORIZED");
        errorBody.put("message", "인증이 필요합니다. 유효한 토큰을 제공해주세요.");
        errorBody.put("status", HttpStatus.UNAUTHORIZED.value());

        objectMapper.writeValue(response.getOutputStream(), errorBody);
    }
}
