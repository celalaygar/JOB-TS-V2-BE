package com.tracker.job_ts.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class MalformedJwtTokenException extends AuthenticationException {
    public MalformedJwtTokenException(String message) {
        super(message);
    }
}
