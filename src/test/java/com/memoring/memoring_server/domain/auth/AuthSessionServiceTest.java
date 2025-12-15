package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.token.RefreshTokenService;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.global.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthSessionServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthSessionService authSessionService;

    @DisplayName("세션 생성 시 액세스/리프레시 토큰을 발급하고 저장한다")
    @Test
    void createSession() {
        User user = User.create("nick", "tester", "pass");
        ReflectionTestUtils.setField(user, "id", 1L);

        given(jwtTokenProvider.generateAccessToken("tester")).willReturn("access");
        given(jwtTokenProvider.generateRefreshToken("tester")).willReturn("refresh");
        given(jwtTokenProvider.getAccessTokenValidityInMillis()).willReturn(1000L);
        given(jwtTokenProvider.getRefreshTokenValidityInMillis()).willReturn(2000L);

        AuthToken token = authSessionService.createSession(user, "tester");

        assertThat(token.accessToken()).isEqualTo("access");
        assertThat(token.refreshToken()).isEqualTo("refresh");
        assertThat(token.tokenType()).isEqualTo("Bearer");
        verify(refreshTokenService).saveRefreshToken(org.mockito.Mockito.eq(user), org.mockito.Mockito.eq("refresh"), org.mockito.Mockito.any());
    }
}
