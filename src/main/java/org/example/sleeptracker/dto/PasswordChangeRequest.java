package org.example.sleeptracker.dto;

import lombok.Builder;
import lombok.Data;
import org.example.sleeptracker.validations.annotations.Password;

@Data
@Builder
public class PasswordChangeRequest {

    private String oldPassword;
    @Password
    private String newPassword;
    private String confirmPassword;

}
