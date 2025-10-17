package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.models.User;

@Data
@Builder
public class AuthResponse {

    private Long id;
    private String username;
    private String email;
    private String token;

    public static AuthResponse forLoginResponse(User user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
    }

}
