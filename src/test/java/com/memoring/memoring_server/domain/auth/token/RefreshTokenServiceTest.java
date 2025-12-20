package com.memoring.memoring_server.domain.auth.token;

import com.memoring.memoring_server.domain.auth.exception.ExpiredRefreshTokenException;
import com.memoring.memoring_server.domain.auth.exception.InvalidRefreshTokenException;
import com.memoring.memoring_server.domain.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @DisplayName("기존 리프레시 토큰이 있으면 갱신한다")
    @Test
    void saveRefreshTokenUpdatesExisting() {
        User user = User.create("nick", "tester", "pass");
        RefreshToken existing = mock(RefreshToken.class);

        given(refreshTokenRepository.findByUser(user)).willReturn(Optional.of(existing));

        refreshTokenService.saveRefreshToken(user, "new-token", Instant.now().plusSeconds(10));

        verify(existing).updateToken(any(), any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @DisplayName("리프레시 토큰이 없으면 새로 저장한다")
    @Test
    void saveRefreshTokenCreatesNew() {
        User user = User.create("nick", "tester", "pass");
        RefreshToken saved = RefreshToken.create(user, "token", Instant.now());

        given(refreshTokenRepository.findByUser(user)).willReturn(Optional.empty());
        given(refreshTokenRepository.save(any(RefreshToken.class))).willReturn(saved);

        refreshTokenService.saveRefreshToken(user, "token", Instant.now());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @DisplayName("유효한 리프레시 토큰을 반환한다")
    @Test
    void getValidRefreshToken() {
        User user = User.create("nick", "tester", "pass");
        RefreshToken refreshToken = RefreshToken.create(user, "token", Instant.now().plusSeconds(60));

        given(refreshTokenRepository.findByToken("token")).willReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.getValidRefreshToken("token");

        assertThat(result).isSameAs(refreshToken);
    }

    @DisplayName("만료된 리프레시 토큰이면 삭제 후 예외를 던진다")
    @Test
    void getValidRefreshTokenFailsWhenExpired() {
        User user = User.create("nick", "tester", "pass");
        RefreshToken refreshToken = RefreshToken.create(user, "token", Instant.now().minusSeconds(1));

        given(refreshTokenRepository.findByToken("token")).willReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> refreshTokenService.getValidRefreshToken("token"))
                .isInstanceOf(ExpiredRefreshTokenException.class);
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @DisplayName("리프레시 토큰이 없으면 예외를 던진다")
    @Test
    void getValidRefreshTokenFailsWhenMissing() {
        given(refreshTokenRepository.findByToken("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> refreshTokenService.getValidRefreshToken("missing"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }
}
