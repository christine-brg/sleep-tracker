package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.validations.annotations.Password;
import org.example.sleeptracker.validations.annotations.PhoneNumber;
import org.example.sleeptracker.validations.annotations.Username;

@Data
@Builder
public class RegisterRequest {
    @Username
    private String username;
    @Username
    private String email;
    @Password
    private String password;
    private String confirmPassword;
    @PhoneNumber
    private String phoneNumber;
}
