package com.tracker.job_ts.auth.dto.emailChange;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailChangeValidationResponse {
    private String token;
    private String newEmailPending;
    private String currentEmail;
    private Boolean valid;
}