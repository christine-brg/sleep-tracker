package org.example.sleeptracker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.models.RoleEnum;
import org.example.sleeptracker.models.User;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private Long id;
    private String username;
    private String email;
    private String token;
    private String refreshToken;
    private boolean enabled;
    private RoleEnum role;
    private Long expiresIn;

    public static AuthResponse forLoginResponse(User user, String token, String refreshToken, long expiresIn) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .refreshToken(refreshToken)
                .enabled(user.isEnabled())
                .role(user.getRole())
                .expiresIn(expiresIn)
                .build();
    }

    public static AuthResponse fromAuthenticatedUser(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }

}
