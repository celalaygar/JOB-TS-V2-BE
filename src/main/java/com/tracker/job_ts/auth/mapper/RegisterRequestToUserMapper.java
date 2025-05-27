package com.tracker.job_ts.auth.mapper;


import com.tracker.job_ts.auth.dto.RegisterRequest;
import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RegisterRequestToUserMapper {

    private final PasswordEncoder passwordEncoder;

    public User map(RegisterRequest request) {
        Set<SystemRole> systemRoles = new HashSet<>();
        systemRoles.add(SystemRole.ROLE_USER);

        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .systemRoles(systemRoles)
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .build();
    }
}
