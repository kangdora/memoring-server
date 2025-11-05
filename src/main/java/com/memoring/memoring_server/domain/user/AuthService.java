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
public class AuthService {

    private static final String TOKEN_TYPE = "Bearer";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public UserLoginResponseDto login(LogInRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.id(), requestDto.password())
        );

        User user = userService.getUserByLoginId(authentication.getName());

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(user, refreshToken);

        return buildTokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public UserSignUpResponseDto signup(SignUpRequestDto requestDto) {
        User user = userService.registerUser(requestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLoginId(), requestDto.password())
        );

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(user, refreshToken);

        return new UserSignUpResponseDto(
                "회원가입에 성공했습니다.",
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                user.getRole(),
                user.getUsername(),
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }

    @Transactional
    public UserLoginResponseDto refresh(TokenRefreshRequestDto requestDto) {
        RefreshToken refreshToken = refreshTokenService.getValidRefreshToken(requestDto.refreshToken());
        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getLoginId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getLoginId());
        saveRefreshToken(user, newRefreshToken);

        return buildTokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String loginId) {
        User user = userService.getUserByLoginId(loginId);
        refreshTokenService.deleteByUser(user);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        Instant expiry = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenValidityInMillis());
        refreshTokenService.saveRefreshToken(user, refreshToken, expiry);
    }

    private UserLoginResponseDto buildTokenResponse(String accessToken, String refreshToken) {
        return new UserLoginResponseDto(
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }
}