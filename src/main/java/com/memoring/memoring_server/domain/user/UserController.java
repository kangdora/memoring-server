package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.SignUpRequest;
import com.memoring.memoring_server.domain.user.dto.UserSignUpResponse;
import com.memoring.memoring_server.domain.user.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserInfoResponse response = userService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping()
    public ResponseEntity<UserSignUpResponse> signup(@RequestBody SignUpRequest request) {
        UserSignUpResponse response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
