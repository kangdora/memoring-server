package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController implements UserApi{

    private final UserService userService;
    private final AuthService authService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserInfoResponse response = userService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody LogInRequest request) {
        UserLoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/refresh")
    public ResponseEntity<UserLoginResponse> refresh(@RequestBody TokenRefreshRequest request) {
        UserLoginResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponse> signup(@RequestBody SignUpRequest request) {
        UserSignUpResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
