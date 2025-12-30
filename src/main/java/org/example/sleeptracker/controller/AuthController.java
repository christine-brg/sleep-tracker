package org.example.sleeptracker.controller;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.sleeptracker.dto.AuthResponse;
import org.example.sleeptracker.dto.LoginRequest;
import org.example.sleeptracker.dto.RegisterRequest;
import org.example.sleeptracker.exceptions.JwtAuthenticationException;
import org.example.sleeptracker.service.Impl.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> loginUser(
            @RequestBody final RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromCookie(request, "refresh_token");

        if (StringUtils.isBlank(refreshToken)) {
            throw new JwtAuthenticationException("Refresh token is missing");
        }

        AuthResponse userDetails = authService.refreshToken(refreshToken);

        response.addHeader("Set-Cookie",
                "refresh_token=" + refreshToken + "; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=604800");

        userDetails.setRefreshToken(null);
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse userDetails = authService.login(request);
        String refreshToken = userDetails.getRefreshToken();
        if (StringUtils.isBlank(refreshToken)) {
            throw new JwtAuthenticationException("Refresh token is missing");
        }
        response.addHeader("Set-Cookie",
                "refresh_token=" + refreshToken + "; HttpOnly; Secure; SameSite=Strict; Path=/; Max-Age=604800");

        userDetails.setRefreshToken(null);
        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    private String extractTokenFromCookie(HttpServletRequest request, String name) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(name)) return cookie.getValue();
        }
        return null;
    }
}
