package com.tracker.job_ts.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordResponse {
    private boolean success;
    private String message;
    private String newPassword; // ⚠️ Opsiyonel, istersen maskelenmiş şekilde dön
    private String newToken;
}