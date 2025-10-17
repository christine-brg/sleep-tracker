package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserRequest {
    private String username;
    private String email;
    private String phoneNumber;
}
