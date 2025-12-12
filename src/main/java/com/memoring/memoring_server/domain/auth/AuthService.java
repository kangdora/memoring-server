package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.dto.*;
import com.memoring.memoring_server.domain.auth.token.RefreshToken;
import com.memoring.memoring_server.domain.auth.token.RefreshTokenService;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import com.memoring.memoring_server.global.config.security.jwt.JwtTokenProvider;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public UserLoginResponse login(LogInRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = userService.getUserByUsername(authentication.getName());

        AuthToken token = createSession(user, user.getUsername());

        return new UserLoginResponse(
                token.accessToken(),
                token.refreshToken(),
                token.tokenType(),
                token.accessTokenExpiresIn(),
                token.refreshTokenExpiresIn()
        );
    }

    public UserLoginResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.getValidRefreshToken(request.refreshToken());
        User user = refreshToken.getUser();

        AuthToken token = createSession(user, user.getUsername());

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

    private void saveRefreshToken(User user, String refreshToken) {
        Instant expiry = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenValidityInMillis());
        refreshTokenService.saveRefreshToken(user, refreshToken, expiry);
    }

    @Transactional
    public AuthToken createSession(User user, String username) {
        String accessToken = jwtTokenProvider.generateAccessToken(username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);
        saveRefreshToken(user, refreshToken);

        return new AuthToken(
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }
}
