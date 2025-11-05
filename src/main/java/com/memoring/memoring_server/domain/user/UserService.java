package com.memoring.memoring_server.domain.user;

import com.memoring.memoring_server.domain.user.dto.*;
import com.memoring.memoring_server.domain.user.token.RefreshToken;
import com.memoring.memoring_server.domain.user.token.RefreshTokenService;
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

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final String TOKEN_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

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

        User user = userRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(user, refreshToken);

        return buildTokenResponse(accessToken, refreshToken);
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
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        saveRefreshToken(user, refreshToken);

        return new UserSignUpResponseDto(
                "회원가입에 성공했습니다.",
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                user.getRole(),
                user.getUsername(),
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }

    @Transactional
    public UserLoginResponseDto refresh(TokenRefreshRequestDto requestDto) {
        RefreshToken refreshToken = refreshTokenService.getValidRefreshToken(requestDto.refreshToken());
        User user = refreshToken.getUser();

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getLoginId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getLoginId());
        saveRefreshToken(user, newRefreshToken);

        return buildTokenResponse(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String loginId) {
        userRepository.findByLoginId(loginId)
                .ifPresent(refreshTokenService::deleteByUser);
    }

    private void validateSignupRequest(SignUpRequestDto requestDto) {
        if (!requestDto.password().equals(requestDto.passwordConfirm())) {
            throw new PasswordMismatchException();
        }

        if (userRepository.existsByLoginId(requestDto.id())) {
            throw new DuplicateLoginIdException();
        }
    }

    private void saveRefreshToken(User user, String refreshToken) {
        Instant expiry = Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenValidityInMillis());
        refreshTokenService.saveRefreshToken(user, refreshToken, expiry);
    }

    private UserLoginResponseDto buildTokenResponse(String accessToken, String refreshToken) {
        return new UserLoginResponseDto(
                accessToken,
                refreshToken,
                TOKEN_TYPE,
                jwtTokenProvider.getAccessTokenValidityInMillis(),
                jwtTokenProvider.getRefreshTokenValidityInMillis()
        );
    }
}
