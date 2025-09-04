package com.tracker.job_ts.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class SecurityJwtTokenException extends AuthenticationException {
    public SecurityJwtTokenException(String message) {
        super(message);
    }
}
