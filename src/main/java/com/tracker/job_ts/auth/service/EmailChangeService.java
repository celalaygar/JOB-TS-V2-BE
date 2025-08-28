package com.tracker.job_ts.auth.service;


import com.tracker.job_ts.auth.config.properties.EmailChangeProperties;
import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.dto.emailChange.EmailChangeRequest;
import com.tracker.job_ts.auth.dto.emailChange.EmailChangeResponse;
import com.tracker.job_ts.auth.dto.emailChange.EmailChangeValidationRequest;
import com.tracker.job_ts.auth.dto.emailChange.EmailChangeValidationResponse;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailChangeService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final AuthHelperService authHelperService;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final EmailChangeProperties emailChangeProperties;


    @Value("${frontend.url}")
    private String frontendUrl;

    /**
     * Sends an 8-character alphanumeric code to the user's email.
     * Enforces a 60-second cooldown between consecutive requests.
     *
     * @return A Mono that completes successfully or with an error.
     */
    public Mono<EmailChangeResponse> sendChangeCode() {
        return authHelperService.getAuthUser()
                .flatMap(user -> {
                    if (user.getEmailVerificationCodeSentAt() != null) {
                        Duration timeElapsed = Duration.between(user.getEmailVerificationCodeSentAt(), LocalDateTime.now());
                        if (timeElapsed.getSeconds() < emailChangeProperties.getCooldownSeconds()) {
                            return Mono.just(new EmailChangeResponse(false,
                                    "Please wait at least " + emailChangeProperties.getCooldownSeconds() + " seconds before requesting a new code."));
                        }
                    }

                    String code = generateAlphanumericCode(8);
                    user.setEmailVerificationCode(code);
                    user.setEmailVerificationCodeSentAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .flatMap(updatedUser -> {
                                String subject = "Your Email Change Verification Code";
                                String content = "Hello,\n\nYour verification code for email change is:"+
                                        " \n\n CODE :           " + code +
                                        "\n\nThis code is valid for a short time. Do not share it with anyone.\n\nRegards,\nYour App Team";

                                return emailService.sendCustomEmail(updatedUser.getEmail(), subject, content)
                                        .thenReturn(new EmailChangeResponse(true,
                                                "Verification code sent successfully. Check your email."));
                            });
                });
    }


    /**
     * Verifies the provided code and password, then saves the new email and token to the user document
     * before sending a confirmation link to the new email address.
     *
     * @param request Contains the current password, new email, and the verification code.
     * @return A Mono that completes successfully or with an error.
     */
    public Mono<EmailChangeResponse> verifyAndGenerateChangeLink(EmailChangeRequest request) {
        return authHelperService.getAuthUser()
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return Mono.just(new EmailChangeResponse(false, "Invalid password."));
                    }

                    if (user.getEmailVerificationCode() == null ||
                            !user.getEmailVerificationCode().equals(request.getVerificationCode())) {
                        return Mono.just(new EmailChangeResponse(false, "Invalid verification code."));
                    }

                    if (user.getEmailVerificationCodeSentAt() == null ||
                            Duration.between(user.getEmailVerificationCodeSentAt(), LocalDateTime.now()).toMinutes() > emailChangeProperties.getCodeValidityMinutes()) {
                        return Mono.just(new EmailChangeResponse(false, "The verification code has expired. Please request a new one."));
                    }

                    // ✅ New email başka bir kullanıcıya ait mi kontrol et
                    return userRepository.findByEmail(request.getNewEmail())
                            .flatMap(existingUser -> {
                                // Eğer başka user bu emaili kullanıyorsa hata dön
                                if (!existingUser.getId().equals(user.getId())) {
                                    return Mono.just(new EmailChangeResponse(false, "This email is already in use."));
                                }
                                return Mono.empty(); // aynı kullanıcıysa akışa devam etsin
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // ✅ Email başka kullanıcıya ait değil, token üret
                                String changeToken = jwtProvider.generateEmailChangeToken(user.getId(), request.getNewEmail());

                                user.setNewEmailPending(request.getNewEmail());
                                user.setEmailChangeToken(changeToken);
                                user.setEmailChangeTokenSentAt(LocalDateTime.now());

                                return userRepository.save(user)
                                        .flatMap(updatedUser -> {
                                            String verificationLink = frontendUrl + "/public/change-mail/token/" + changeToken;
                                            String subject = "Confirm Your New Email Address";
                                            String content = "Hello,\n\nTo complete the email change process, please click the link below:\n\n" +
                                                    verificationLink + "\n\nThis link is valid for a short time.\n\nRegards,\nYour App Team";

                                            return emailService.sendCustomEmail(updatedUser.getNewEmailPending(), subject, content)
                                                    .thenReturn(new EmailChangeResponse(true, "Verification link sent to your new email address."));
                                        });
                            }));
                });
    }



    /**
     * Validates the provided email change token and returns the email change details.
     * This method does not perform the final email change; it only validates the token.
     *
     * @param request Contains the token.
     * @return A Mono of EmailChangeValidationResponse with details about the token.
     */
    public Mono<EmailChangeValidationResponse> validateToken(EmailChangeValidationRequest request) {
        return userRepository.findByEmailChangeToken(request.getToken())
                .flatMap(user -> {
                    // Check if token is expired
                    boolean isValid = Duration.between(
                            user.getEmailChangeTokenSentAt(), LocalDateTime.now()).toMinutes() < emailChangeProperties.getTokenValidityMinutes();

                    if (isValid) {
                        return Mono.just(
                                new EmailChangeValidationResponse(
                                        user.getEmailChangeToken(),
                                        user.getNewEmailPending(),
                                        user.getEmail(),
                                        true));
                    } else {
                        // If token is invalid/expired, we can clear the pending state
                        user.setNewEmailPending(null);
                        user.setEmailChangeToken(null);
                        user.setEmailChangeTokenSentAt(null);
                        return userRepository.save(user)
                                .thenReturn(
                                        new EmailChangeValidationResponse(
                                                request.getToken(), null,null, false));
                    }
                })
                .switchIfEmpty(Mono.just(
                        new EmailChangeValidationResponse(
                                request.getToken(), null, null,false)));
    }

    /**
     * Finalizes the email change process by confirming it.
     * @param token The email change token.
     * @return A Mono of EmailChangeResponse indicating the result.
     */
    public Mono<EmailChangeResponse> confirmEmailChange(String token) {
        return userRepository.findByEmailChangeToken(token)
                .flatMap(user -> {
                    if (user.getEmailChangeTokenSentAt() == null ||
                            Duration.between(user.getEmailChangeTokenSentAt(), LocalDateTime.now()).toMinutes() > emailChangeProperties.getTokenValidityMinutes()) {
                        return Mono.just(new EmailChangeResponse(false, "The email change token has expired."));
                    }

                    // Final email update
                    user.setEmail(user.getNewEmailPending());
                    user.setNewEmailPending(null);
                    user.setEmailChangeToken(null);
                    user.setEmailChangeTokenSentAt(null);

                    return userRepository.save(user)
                            .thenReturn(new EmailChangeResponse(true, "Email has been successfully updated."));
                })
                .switchIfEmpty(Mono.just(new EmailChangeResponse(false, "Invalid email change token.")));
    }

    /**
     * Rejects the email change request and clears the pending fields.
     * @param token The email change token.
     * @return A Mono of EmailChangeResponse indicating the result.
     */
    public Mono<EmailChangeResponse> rejectEmailChange(String token) {
        return userRepository.findByEmailChangeToken(token)
                .flatMap(user -> {
                    // Clear pending email change fields
                    user.setNewEmailPending(null);
                    user.setEmailChangeToken(null);
                    user.setEmailChangeTokenSentAt(null);

                    return userRepository.save(user)
                            .thenReturn(new EmailChangeResponse(true, "Email change request has been rejected."));
                })
                .switchIfEmpty(Mono.just(new EmailChangeResponse(false, "Invalid email change token.")));
    }

    /**
     * Generates a random alphanumeric code of a specified length.
     *
     * @param length The desired length of the code.
     * @return The generated code string.
     */
    private String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZO123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString().toUpperCase();
    }
}