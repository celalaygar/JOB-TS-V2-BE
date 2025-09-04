package com.tracker.job_ts.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class UnsupportedJwtTokenException extends AuthenticationException {
    public UnsupportedJwtTokenException(String message) {
        super(message);
    }
}
