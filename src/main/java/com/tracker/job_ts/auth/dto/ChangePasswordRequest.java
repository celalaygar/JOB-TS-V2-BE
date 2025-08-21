package com.tracker.job_ts.auth.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}