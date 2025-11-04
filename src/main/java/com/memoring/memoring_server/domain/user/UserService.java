package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.*;
import com.memoring.memoring_server.global.config.security.jwt.JwtTokenProvider;
import com.memoring.memoring_server.global.exception.DuplicateLoginIdException;
import com.memoring.memoring_server.global.exception.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getLoginId())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }

    public UserInfoResponseDto getUserInfo(String loginId) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return UserInfoResponseDto.from(user);
    }

    public UserLoginResponseDto login(LogInRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.id(), requestDto.password())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        return new UserLoginResponseDto(accessToken, TOKEN_TYPE);
    }

    @Transactional
    public UserSignUpResponseDto signup(SignUpRequestDto requestDto) {
        validateSignupRequest(requestDto);

        User user = User.builder()
                .username(requestDto.username())
                .loginId(requestDto.id())
                .password(passwordEncoder.encode(requestDto.password()))
                .role(Role.MAJOR)
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getLoginId(), requestDto.password())
        );
        String accessToken = jwtTokenProvider.generateToken(authentication);

        return new UserSignUpResponseDto(
                "회원가입에 성공했습니다.",
                accessToken,
                user.getRole(),
                user.getUsername()
        );
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
