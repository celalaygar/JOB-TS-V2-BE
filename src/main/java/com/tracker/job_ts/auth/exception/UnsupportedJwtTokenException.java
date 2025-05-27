package com.tracker.job_ts.auth.exception;

public class UnsupportedJwtTokenException extends RuntimeException {
    public UnsupportedJwtTokenException(String message) {
        super(message);
    }
}
