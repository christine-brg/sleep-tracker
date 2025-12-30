package org.example.sleeptracker.service.Impl;

import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.sleeptracker.config.SecurityConfig;
import org.example.sleeptracker.dto.AuthResponse;
import org.example.sleeptracker.dto.LoginRequest;
import org.example.sleeptracker.dto.RegisterRequest;
import org.example.sleeptracker.exceptions.JwtAuthenticationException;
import org.example.sleeptracker.models.RoleEnum;
import org.example.sleeptracker.models.TokenType;
import org.example.sleeptracker.models.User;
import org.example.sleeptracker.repository.UserRepository;
import org.example.sleeptracker.security.SecurityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already used");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .enabled(true)
                .role(RoleEnum.USER)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String token = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        long expiresIn = jwtService.getTokenRemainingSeconds(token, TokenType.ACCESS_TOKEN);

        return AuthResponse.forLoginResponse(user, token, refreshToken, expiresIn);
    }

    public AuthResponse refreshToken(String jwtToken) {

        if (StringUtils.isBlank(jwtToken)) {
            throw new JwtAuthenticationException("Jwt token is missing");
        }

        if (!jwtService.isTokenValid(jwtToken, TokenType.REFRESH_TOKEN)) {
            throw new JwtAuthenticationException("Jwt token is invalid or expired");
        }

        User user = getUser(jwtService.getUsername(jwtToken, TokenType.REFRESH_TOKEN));
        AuthResponse authenticatedUserDetails = AuthResponse.fromAuthenticatedUser(user);

        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);
        long expiresIn = jwtService.getTokenRemainingSeconds(accessToken, TokenType.ACCESS_TOKEN);

        authenticatedUserDetails.setToken(accessToken);
        authenticatedUserDetails.setRefreshToken(refreshToken);
        authenticatedUserDetails.setExpiresIn(expiresIn);
        return authenticatedUserDetails;
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("No such "+ username +" user found.")
        );
    }

}

