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

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = getUserByLoginId(loginId);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public UserInfoResponseDto getUserInfo(String loginId) {
        User user = getUserByLoginId(loginId);

        return UserInfoResponseDto.from(user);
    }

    @Transactional
    public User registerUser(SignUpRequestDto requestDto) {
        validateSignupRequest(requestDto);

        User user = User.builder()
                .username(requestDto.username())
                .loginId(requestDto.id())
                .password(passwordEncoder.encode(requestDto.password()))
                .role(Role.MAJOR)
                .build();

        return userRepository.save(user);
    }

    public User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    private void validateSignupRequest(SignUpRequestDto requestDto) {
        if (!requestDto.password().equals(requestDto.passwordConfirm())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByLoginId(requestDto.id())) {
            throw new DuplicateLoginIdException();
        }
    }

}
