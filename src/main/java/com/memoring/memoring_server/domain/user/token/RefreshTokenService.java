package com.memoring.memoring_server.domain.user.token;

import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.global.exception.ExpiredRefreshTokenException;
import com.memoring.memoring_server.global.exception.InvalidRefreshTokenException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(User user, String token, Instant expiryDate) {
        return refreshTokenRepository.findByUser(user)
                .map(existing -> {
                    existing.updateToken(token, expiryDate);
                    return existing;
                })
                .orElseGet(() -> refreshTokenRepository.save(
                        RefreshToken.builder()
                                .user(user)
                                .token(token)
                                .expiryDate(expiryDate)
                                .build()
                ));
    }

    public RefreshToken getValidRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(InvalidRefreshTokenException::new);

        if (refreshToken.isExpired(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new ExpiredRefreshTokenException();
        }

        return refreshToken;
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}