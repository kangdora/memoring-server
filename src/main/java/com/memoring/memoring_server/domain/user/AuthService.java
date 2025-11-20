package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.*;
import com.memoring.memoring_server.domain.user.token.RefreshToken;
import com.memoring.memoring_server.domain.user.token.RefreshTokenService;
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

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(user, refreshToken);

        return buildTokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public UserSignUpResponse signup(SignUpRequest request) {
        User user = userService.registerUser(request);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), request.password())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(user, refreshToken);

        return new UserSignUpResponse(
                "회원가입에 성공했습니다.",
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                user.getUsername(),
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }

    @Transactional
    public UserLoginResponse refresh(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenService.getValidRefreshToken(request.refreshToken());
        User user = refreshToken.getUser();

        String username = user.getUsername();

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        saveRefreshToken(user, newRefreshToken);

        return buildTokenResponse(newAccessToken, newRefreshToken);
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

    private UserLoginResponse buildTokenResponse(String accessToken, String refreshToken) {
        return new UserLoginResponse(
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }
}
