package com.tracker.job_ts.project.exception.projectTaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;

    public ErrorResponse(ErrorCode errorCode, String message) {
        this.code = errorCode.name();
        this.message = message;
    }
}