package com.memoring.memoring_server.domain.auth;

import com.memoring.memoring_server.domain.auth.dto.LogInRequest;
import com.memoring.memoring_server.domain.auth.dto.TokenRefreshRequest;
import com.memoring.memoring_server.domain.auth.dto.UserLoginResponse;
import com.memoring.memoring_server.domain.auth.token.RefreshToken;
import com.memoring.memoring_server.domain.auth.token.RefreshTokenService;
import com.memoring.memoring_server.domain.user.User;
import com.memoring.memoring_server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @Mock
    private AuthSessionService authSessionService;

    @InjectMocks
    private AuthService authService;

    @DisplayName("로그인 시 인증 후 세션 토큰을 반환한다")
    @Test
    void login() {
        LogInRequest request = new LogInRequest("tester", "pass");
        Authentication authentication = mock(Authentication.class);
        User user = User.create("nick", "tester", "encoded");
        ReflectionTestUtils.setField(user, "id", 1L);
        AuthToken token = new AuthToken("access", "refresh", "Bearer", 1000L, 2000L);

        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willReturn(authentication);
        given(authentication.getName()).willReturn("tester");
        given(userService.getUserByUsername("tester")).willReturn(user);
        given(authSessionService.createSession(user, "tester")).willReturn(token);

        UserLoginResponse response = authService.login(request);

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh");
    }

    @DisplayName("리프레시 토큰으로 세션을 갱신한다")
    @Test
    void refresh() {
        TokenRefreshRequest request = new TokenRefreshRequest("refresh-token");
        User user = User.create("nick", "tester", "encoded");
        AuthToken token = new AuthToken("access", "refresh2", "Bearer", 1000L, 2000L);

        RefreshToken refreshToken = RefreshToken.create(user, "refresh-token", java.time.Instant.now().plusSeconds(60));

        given(refreshTokenService.getValidRefreshToken("refresh-token")).willReturn(refreshToken);
        given(authSessionService.createSession(user, "tester")).willReturn(token);

        UserLoginResponse response = authService.refresh(request);

        assertThat(response.accessToken()).isEqualTo("access");
        assertThat(response.refreshToken()).isEqualTo("refresh2");
    }

    @DisplayName("로그아웃 시 사용자 리프레시 토큰을 삭제한다")
    @Test
    void logout() {
        User user = User.create("nick", "tester", "encoded");

        given(userService.getUserByUsername("tester")).willReturn(user);

        authService.logout("tester");

        verify(refreshTokenService).deleteByUser(eq(user));
    }
}
