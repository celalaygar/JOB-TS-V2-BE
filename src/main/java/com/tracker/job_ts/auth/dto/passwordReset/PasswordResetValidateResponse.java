package com.tracker.job_ts.auth.dto.passwordReset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetValidateResponse {
    private String token;
    private Boolean valid;
    private String email;
    private String message;
}