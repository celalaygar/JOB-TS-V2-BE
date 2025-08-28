package com.tracker.job_ts.auth.service;

import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.dto.ChangePasswordRequest;
import com.tracker.job_ts.auth.dto.ChangePasswordResponse;
import com.tracker.job_ts.auth.dto.RegisterRequest;
import com.tracker.job_ts.auth.dto.UserDto;
import com.tracker.job_ts.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final TokenLogService tokenLogService;
    private final AuthHelperService authHelperService;
    private final UserValidationService validationService;

    public Mono<UserDto> updateMe(RegisterRequest dto) {
        return validationService.validateUpdate(dto)
                .then(authHelperService.getAuthUser())
                .flatMap(authUser -> userRepository.findById(authUser.getId()))
                .flatMap(user -> {
                    if (dto.getUsername() != null) {
                        user.setUsername(dto.getUsername());
                    }
                    if (dto.getFirstname() != null) {
                        user.setFirstname(dto.getFirstname());
                    }
                    if (dto.getLastname() != null) {
                        user.setLastname(dto.getLastname());
                    }
                    if (dto.getPhone() != null) {
                        user.setPhone(dto.getPhone());
                    }
                    if (dto.getDateOfBirth() != null) {
                        user.setDateOfBirth(dto.getDateOfBirth());
                    }
                    user.setGender(dto.getGender());
                    user.setPosition(dto.getPosition());
                    user.setDepartment(dto.getDepartment());

                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                })
                .map(UserDto::new);
    }

    /**
     * Updates the password of the authenticated user.
     *
     * @param request Contains the current and new password details.
     * @return ChangePasswordResponse with status, message, masked password and new token.
     */
    public Mono<ChangePasswordResponse> changePassword(ChangePasswordRequest request) {
        return Mono.just(request)
                .flatMap(req -> {
                    if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
                        return Mono.error(new IllegalArgumentException("New passwords do not match."));
                    }
                    return Mono.empty();
                })
                .then(authHelperService.getAuthUser())
                .flatMap(authUser -> userRepository.findById(authUser.getId()))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return Mono.error(new IllegalArgumentException("Current password is incorrect."));
                    }

                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .flatMap(savedUser -> {
                                String newToken = jwtProvider.generateToken(
                                        savedUser.getEmail(),
                                        savedUser.getSystemRoles().stream().toList()
                                );

                                tokenLogService.logToken(savedUser, newToken, Instant.now());

                                return Mono.just(new ChangePasswordResponse(
                                        true,
                                        "Password has been changed successfully.",
                                        user.getPassword(),
                                        newToken
                                ));
                            });
                })
                .onErrorResume(e -> {
                    log.error("Password change error: {}", e.getMessage());
                    return Mono.just(new ChangePasswordResponse(
                            false,
                            e.getMessage(),
                            null,
                            null
                    ));
                });
    }
}
