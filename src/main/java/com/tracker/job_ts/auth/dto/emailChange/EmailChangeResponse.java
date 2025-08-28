package com.tracker.job_ts.auth.dto.emailChange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailChangeResponse {
    private Boolean success;
    private String message;
}