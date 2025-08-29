package com.tracker.job_ts.auth.service;


import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.config.properties.PasswordResetProperties;
import com.tracker.job_ts.auth.dto.passwordReset.PasswordResetResponse;
import com.tracker.job_ts.auth.dto.passwordReset.PasswordResetValidateRequest;
import com.tracker.job_ts.auth.dto.passwordReset.PasswordResetValidateResponse;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetProperties props;

    @Value("${frontend.url}")
    private String frontendUrl;

    public Mono<PasswordResetResponse> sendResetMail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    if (user.getResetPasswordTokenSentAt() != null &&
                            Duration.between(user.getResetPasswordTokenSentAt(), LocalDateTime.now()).getSeconds() < props.getCooldownSeconds()) {
                        return Mono.just(new PasswordResetResponse(false,
                                "Please wait before requesting another reset email."));
                    }

                    // JWT token oluştur
                    String token = jwtProvider.generateResetPasswordToken(user.getId());

                    user.setResetPasswordToken(token);
                    user.setResetPasswordTokenSentAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .flatMap(u -> {
                                String link =frontendUrl + "public/password-reset/token/" + token;
                                String subject = "Password Reset Request";
                                String content = "Hello,\n\nClick the link to reset your password:\n" + link +
                                        "\n\nThis link is valid for " + props.getTokenValidityMinutes() + " minutes.";
                                return emailService.sendCustomEmail(u.getEmail(), subject, content)
                                        .thenReturn(new PasswordResetResponse(true, "Password reset link sent to email."));
                            });
                })
                .switchIfEmpty(Mono.just(new PasswordResetResponse(false, "No user found with this email.")));
    }

    public Mono<PasswordResetValidateResponse> validateToken(PasswordResetValidateRequest request) {
        return userRepository.findByResetPasswordToken(request.getToken())
                .flatMap(user -> {
                    boolean valid = jwtProvider.validateResetPasswordToken(request.getToken()) &&
                            Duration.between(user.getResetPasswordTokenSentAt(), LocalDateTime.now()).toMinutes() < props.getTokenValidityMinutes();
                    return Mono.just(new PasswordResetValidateResponse(request.getToken(), valid, user.getEmail()));
                })
                .switchIfEmpty(Mono.just(new PasswordResetValidateResponse(request.getToken(), false, null)));
    }

    public Mono<PasswordResetResponse> resetPassword(String token, String newPassword) {
        return userRepository.findByResetPasswordToken(token)
                .flatMap(user -> {
                    boolean valid = jwtProvider.validateResetPasswordToken(token) &&
                            Duration.between(user.getResetPasswordTokenSentAt(), LocalDateTime.now())
                                    .toMinutes() < props.getTokenValidityMinutes();

                    if (!valid) {
                        return Mono.just(new PasswordResetResponse(false, "Token expired or invalid."));
                    }

                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetPasswordToken(null);
                    user.setResetPasswordTokenSentAt(null);

                    return userRepository.save(user)
                            .flatMap(u -> {
                                String subject = "Password Changed Successfully";
                                String content = "Hello,\n\nYour password has been successfully changed.";
                                return emailService.sendCustomEmail(u.getEmail(), subject, content)
                                        .thenReturn(new PasswordResetResponse(true, "Password updated successfully."));
                            });
                })
                .switchIfEmpty(Mono.just(new PasswordResetResponse(false, "Invalid token.")));
    }
}
