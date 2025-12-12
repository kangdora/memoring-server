package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private final AuthService authService;

    @Override
    @PostMapping("/sessions")
    public ResponseEntity<UserLoginResponse> login(@RequestBody LogInRequest request) {
        UserLoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/sessions/current")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/tokens/refresh")
    public ResponseEntity<UserLoginResponse> refresh(@RequestBody TokenRefreshRequest request) {
        UserLoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }
}
