package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.auth.AuthSessionService;
import com.memoring.memoring_server.domain.auth.AuthToken;
import com.memoring.memoring_server.domain.user.dto.SignUpRequest;
import com.memoring.memoring_server.domain.user.dto.UserInfoResponse;
import com.memoring.memoring_server.domain.user.dto.UserSignUpResponse;
import com.memoring.memoring_server.domain.user.exception.DuplicateLoginIdException;
import com.memoring.memoring_server.domain.user.exception.InvalidPasswordFormatException;
import com.memoring.memoring_server.domain.user.exception.InvalidUsernameFormatException;
import com.memoring.memoring_server.domain.user.exception.PasswordMismatchException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AuthSessionService authSessionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @DisplayName("회원가입 시 사용자 등록 후 세션 토큰을 반환한다")
    @Test
    void signup() {
        SignUpRequest request = new SignUpRequest("nick", "user1", "Passw0rd!", "Passw0rd!");
        User user = User.create("nick", "user1", "encoded");
        AuthToken token = new AuthToken("access", "refresh", "Bearer", 100L, 200L);

        given(passwordEncoder.encode("Passw0rd!")).willReturn("encoded");
        given(userRepository.existsByUsername("user1")).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(authSessionService.createSession(user, "user1")).willReturn(token);

        UserSignUpResponse response = userService.signup(request);

        assertThat(response.username()).isEqualTo("user1");
        assertThat(response.accessToken()).isEqualTo("access");
        verify(userRepository).save(any(User.class));
    }

    @DisplayName("회원 정보 조회를 반환한다")
    @Test
    void getUserInfo() {
        User user = User.create("nick", "user1", "pass");
        given(userRepository.findByUsername("user1")).willReturn(Optional.of(user));

        UserInfoResponse response = userService.getUserInfo("user1");

        assertThat(response.username()).isEqualTo("user1");
        assertThat(response.nickname()).isEqualTo("nick");
    }

    @DisplayName("존재하지 않는 사용자는 조회 시 예외가 발생한다")
    @Test
    void getUserByUsernameFailsWhenMissing() {
        given(userRepository.findByUsername("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    @DisplayName("아이디 패턴이 올바르지 않으면 예외가 발생한다")
    @Test
    void signupFailsWhenInvalidUsername() {
        SignUpRequest request = new SignUpRequest("nick", "12", "Passw0rd!", "Passw0rd!");

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(InvalidUsernameFormatException.class);
    }

    @DisplayName("비밀번호 패턴이 올바르지 않으면 예외가 발생한다")
    @Test
    void signupFailsWhenInvalidPassword() {
        SignUpRequest request = new SignUpRequest("nick", "user1", "password", "password");

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(InvalidPasswordFormatException.class);
    }

    @DisplayName("비밀번호 확인이 일치하지 않으면 예외가 발생한다")
    @Test
    void signupFailsWhenPasswordMismatch() {
        SignUpRequest request = new SignUpRequest("nick", "user1", "Passw0rd!", "Passw0rd?");

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(PasswordMismatchException.class);
    }

    @DisplayName("중복 아이디가 존재하면 예외가 발생한다")
    @Test
    void signupFailsWhenDuplicateUsername() {
        SignUpRequest request = new SignUpRequest("nick", "user1", "Passw0rd!", "Passw0rd!");

        given(userRepository.existsByUsername("user1")).willReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(DuplicateLoginIdException.class);
    }
}
