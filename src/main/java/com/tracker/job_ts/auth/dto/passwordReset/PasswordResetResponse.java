package com.tracker.job_ts.auth.dto.passwordReset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponse {
    private Boolean success;
    private String message;

}