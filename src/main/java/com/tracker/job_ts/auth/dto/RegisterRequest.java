package com.tracker.job_ts.auth.dto;

import com.tracker.job_ts.auth.entity.SystemRole;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.Set;

@Data
@ToString
public class RegisterRequest {
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String confirmPassword;
    private String phone;
    private Date dateOfBirth;
    private String gender;
}