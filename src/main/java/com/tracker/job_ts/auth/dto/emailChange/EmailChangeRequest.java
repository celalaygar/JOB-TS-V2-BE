package com.tracker.job_ts.auth.dto.emailChange;

import lombok.Data;

@Data
public class EmailChangeRequest {
    private String currentPassword;
    private String newEmail;
    private String verificationCode;
}