package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.dto.*;
import com.memoring.memoring_server.domain.auth.token.RefreshToken;
import com.memoring.memoring_server.domain.auth.token.RefreshTokenService;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final AuthSessionService authSessionService;

    @Transactional
    public UserLoginResponse login(LogInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userService.getUserByUsername(authentication.getName());

        AuthToken token = authSessionService.createSession(user, user.getUsername());

        return new UserLoginResponse(
                token.accessToken(),
                token.refreshToken(),
                token.tokenType(),
                token.accessTokenExpiresIn(),
                token.refreshTokenExpiresIn()
        );
    }

    @Transactional
    public UserLoginResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.getValidRefreshToken(request.refreshToken());
        User user = refreshToken.getUser();

        AuthToken token = authSessionService.createSession(user, user.getUsername());

        return new UserLoginResponse(
                token.accessToken(),
                token.refreshToken(),
                token.tokenType(),
                token.accessTokenExpiresIn(),
                token.refreshTokenExpiresIn()
        );
    }

    @Transactional
    public void logout(String loginId) {
        User user = userService.getUserByUsername(loginId);
        refreshTokenService.deleteByUser(user);
    }
}
