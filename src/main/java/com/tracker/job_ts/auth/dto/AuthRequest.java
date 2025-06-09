package com.tracker.job_ts.auth.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthRequest {
    private String email;
    private String username;
    private String password;
}
