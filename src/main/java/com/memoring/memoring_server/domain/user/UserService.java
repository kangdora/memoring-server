package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.*;
import com.memoring.memoring_server.global.exception.DuplicateLoginIdException;
import com.memoring.memoring_server.global.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getUserByUsername(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public UserInfoResponseDto getUserInfo(String username) {
        User user = getUserByUsername(username);

        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public User registerUser(SignUpRequestDto requestDto) {
        validateSignupRequest(requestDto);

        User user = User.create(
                requestDto.nickname(),
                requestDto.username(),
                passwordEncoder.encode(requestDto.password())
        );

        return userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateSignupRequest(SignUpRequestDto requestDto) {
        if (!requestDto.password().equals(requestDto.passwordConfirm())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByUsername(requestDto.username())) {
            throw new DuplicateLoginIdException();
        }
    }

}
