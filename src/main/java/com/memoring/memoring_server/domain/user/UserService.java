package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.*;
import com.memoring.memoring_server.global.exception.DuplicateLoginIdException;
import com.memoring.memoring_server.global.exception.InvalidUsernameFormatException;
import com.memoring.memoring_server.global.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String USERNAME_PATTERN = "^(?!\\d+$)[a-z0-9_-]{4,16}$";

    public UserInfoResponse getUserInfo(String username) {
        User user = getUserByUsername(username);
        return UserInfoResponse.from(user);
    }

    @Transactional
    public User registerUser(SignUpRequest request) {
        validateSignupRequest(request);
        User user = User.create(
                request.nickname(),
                request.username(),
                passwordEncoder.encode(request.password())
        );

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateSignupRequest(SignUpRequest request) {
        if (request.username() == null || !request.username().matches(USERNAME_PATTERN)) {
            throw new InvalidUsernameFormatException();
        }

        if (!request.password().equals(request.passwordConfirm())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateLoginIdException();
        }
    }

}
