package com.tracker.job_ts.auth.exception;

public class MalformedJwtTokenException extends RuntimeException {
    public MalformedJwtTokenException(String message) {
        super(message);
    }
}
