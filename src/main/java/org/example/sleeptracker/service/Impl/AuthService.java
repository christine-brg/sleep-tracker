package org.example.sleeptracker.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.sleeptracker.config.SecurityConfig;
import org.example.sleeptracker.dto.AuthResponse;
import org.example.sleeptracker.dto.LoginRequest;
import org.example.sleeptracker.dto.RegisterRequest;
import org.example.sleeptracker.models.RoleEnum;
import org.example.sleeptracker.models.User;
import org.example.sleeptracker.repository.UserRepository;
import org.example.sleeptracker.security.SecurityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        String token = jwtService.createToken(user);
        return AuthResponse.forLoginResponse(user, token);
    }


}

