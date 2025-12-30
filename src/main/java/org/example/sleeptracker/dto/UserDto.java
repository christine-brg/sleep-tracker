package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.models.User;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private boolean active;
    private LocalDateTime createdAt;

    public static UserDto fromUser(final User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isEnabled())
                .build();
    }

    public static UserDto fromUserStatistic(final User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .active(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
