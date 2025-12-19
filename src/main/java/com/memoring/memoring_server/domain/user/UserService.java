package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.auth.AuthSessionService;
import com.memoring.memoring_server.domain.auth.AuthToken;
import com.memoring.memoring_server.domain.user.dto.*;
import com.memoring.memoring_server.domain.user.exception.DuplicateLoginIdException;
import com.memoring.memoring_server.domain.user.exception.InvalidPasswordFormatException;
import com.memoring.memoring_server.domain.user.exception.InvalidUsernameFormatException;
import com.memoring.memoring_server.domain.user.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final AuthSessionService authSessionService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USERNAME_PATTERN = "^(?!\\d+$)[a-z0-9_-]{4,16}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,20}$";

    public UserInfoResponse getUserInfo(String username) {
        User user = getUserByUsername(username);
        return UserInfoResponse.from(user);
    }

    @Transactional
    public UserSignUpResponse signup(SignUpRequest request){
        User user = registerUser(request);
        String username = user.getUsername();

        AuthToken tokens = authSessionService.createSession(user, username);

        return new UserSignUpResponse(
                "회원가입에 성공했습니다.",
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.tokenType(),
                username,
                tokens.accessTokenExpiresIn(),
                tokens.refreshTokenExpiresIn()
        );
    }

    private User registerUser(SignUpRequest request) {
        validateSignupRequest(request);
        User user = User.create(
                request.nickname(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.role(),
                request.address()
        );

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateSignupRequest(SignUpRequest request) {
        if (request.username() == null || !request.username().matches(USERNAME_PATTERN)) {
            throw new InvalidUsernameFormatException();
        }

        if (request.password() == null || !request.password().matches(PASSWORD_PATTERN)){
            throw new InvalidPasswordFormatException();
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateLoginIdException();
        }
    }
}
