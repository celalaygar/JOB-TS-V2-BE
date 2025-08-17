package com.tracker.job_ts.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenValidationResponse {
    private Boolean valid;
    private String token;
    private String invitedUserEmail;
    private String message; // Hata veya bilgilendirme mesajı için
}