package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.token.RefreshTokenService;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.global.config.security.jwt.JwtTokenProvider;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private static final String TOKEN_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

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

    private void saveRefreshToken(User user, String refreshToken) {
        Instant expiry = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenValidityInMillis());
        refreshTokenService.saveRefreshToken(user, refreshToken, expiry);
    }
}
