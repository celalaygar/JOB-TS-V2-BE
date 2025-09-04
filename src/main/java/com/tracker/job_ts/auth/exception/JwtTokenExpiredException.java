package com.tracker.job_ts.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtTokenExpiredException extends AuthenticationException {
    public JwtTokenExpiredException(String message) {
        super(message);
    }
}
