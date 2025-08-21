package com.tracker.job_ts.auth.dto;

import lombok.Data;

@Data
public class EmailChangeValidationRequest {
    private String token;
    private boolean validation;
}