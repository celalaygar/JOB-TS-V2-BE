package com.tracker.job_ts.auth.exception;
import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {

    private final String type;

    public JwtAuthenticationException(String msg, String type) {
        super(msg);
        this.type = type;
    }

    public JwtAuthenticationException(String msg, Throwable cause, String type) {
        super(msg, cause);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}