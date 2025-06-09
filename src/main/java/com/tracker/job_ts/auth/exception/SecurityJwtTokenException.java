package com.tracker.job_ts.auth.exception;

public class SecurityJwtTokenException extends RuntimeException {
    public SecurityJwtTokenException(String message) {
        super(message);
    }
}
