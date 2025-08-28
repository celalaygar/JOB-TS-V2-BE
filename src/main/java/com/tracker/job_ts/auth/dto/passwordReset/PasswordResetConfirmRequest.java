package com.tracker.job_ts.auth.dto.passwordReset;

import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    private String token;
    private String newPassword;
    private String confirmNewPassword;
}