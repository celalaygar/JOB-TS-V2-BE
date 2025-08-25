package com.tracker.job_ts.auth.service;


import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.dto.EmailChangeRequest;
import com.tracker.job_ts.auth.dto.EmailChangeValidationRequest;
import com.tracker.job_ts.auth.dto.EmailChangeValidationResponse;
import com.tracker.job_ts.auth.entity.User;
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

    private static final long COOLDOWN_SECONDS = 60;
    private static final long CODE_VALIDITY_MINUTES = 10;
    private static final long TOKEN_VALIDITY_MINUTES = 15;


    @Value("${frontend.url}")
    private String frontendUrl;

    /**
     * Sends an 8-character alphanumeric code to the user's email.
     * Enforces a 60-second cooldown between consecutive requests.
     *
     * @return A Mono<Boolean> -> true if sent successfully, error otherwise
     */
    public Mono<Boolean> sendChangeCode() {
        return authHelperService.getAuthUser()
                .flatMap(user -> {
                    // Check for cooldown using the timestamp in the user document
                    if (user.getEmailVerificationCodeSentAt() != null) {
                        Duration timeElapsed = Duration.between(user.getEmailVerificationCodeSentAt(), LocalDateTime.now());
                        if (timeElapsed.getSeconds() < COOLDOWN_SECONDS) {
                            return Mono.error(new IllegalStateException("Please wait at least " + COOLDOWN_SECONDS + " seconds before requesting a new code."));
                        }
                    }

                    // Generate the 8-character code
                    String code = generateAlphanumericCode(8);

                    // Update the user document with the new code and timestamp
                    user.setEmailVerificationCode(code);
                    user.setEmailVerificationCodeSentAt(LocalDateTime.now());

                    // Save the user entity to the database
                    return userRepository.save(user)
                            .flatMap(updatedUser -> {
                                // Send the email with the generated code
                                String subject = "Your Email Change Verification Code";
                                String content = "Hello,\n\nYour verification code for email change is: " + code +
                                        "\n\nThis code is valid for a short time. Do not share it with anyone." +
                                        "\n\nRegards,\nYour App Team";

                                return emailService.sendCustomEmail(updatedUser.getEmail(), subject, content)
                                        .thenReturn(true); // ✅ Başarılı olursa true döner
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
    public Mono<Boolean> verifyAndGenerateChangeLink(EmailChangeRequest request) {
        return authHelperService.getAuthUser()
                .flatMap(user -> {
                    // 1. Verify current password
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return Mono.error(new IllegalArgumentException("Invalid password."));
                    }

                    // 2. Verify the provided code and check its validity period
                    if (user.getEmailVerificationCode() == null || !user.getEmailVerificationCode().equals(request.getVerificationCode())) {
                        return Mono.error(new IllegalArgumentException("Invalid verification code."));
                    }
                    if (user.getEmailVerificationCodeSentAt() == null ||
                            Duration.between(user.getEmailVerificationCodeSentAt(), LocalDateTime.now()).toMinutes() > CODE_VALIDITY_MINUTES) {
                        return Mono.error(new IllegalArgumentException("The verification code has expired. Please request a new one."));
                    }

                    // 3. Generate a token for the new email address and save to user document
                    String changeToken = jwtProvider.generateEmailChangeToken(user.getId(), request.getNewEmail());

                    user.setNewEmailPending(request.getNewEmail());
                    user.setEmailChangeToken(changeToken);
                    user.setEmailChangeTokenSentAt(LocalDateTime.now());

                    return userRepository.save(user)
                            .flatMap(updatedUser -> {
                                // 4. Construct the full verification link
                                String verificationLink = frontendUrl + "/change-mail/token/" + changeToken;

                                // 5. Send the email to the new address with the verification link
                                String subject = "Confirm Your New Email Address";
                                String content = "Hello,\n\nTo complete the email change process, please click the link below:\n\n"
                                        + verificationLink
                                        + "\n\nThis link is valid for a short time.\n\nRegards,\nYour App Team";

                                return emailService.sendCustomEmail(updatedUser.getNewEmailPending(), subject, content)
                                        .thenReturn(true); // ✅ başarı durumunda true dönüyor
                            });
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
                    boolean isValid = Duration.between(user.getEmailChangeTokenSentAt(), LocalDateTime.now()).toMinutes() < TOKEN_VALIDITY_MINUTES;

                    if (isValid) {
                        return Mono.just(new EmailChangeValidationResponse(user.getEmailChangeToken(), user.getNewEmailPending(), true));
                    } else {
                        // If token is invalid/expired, we can clear the pending state
                        user.setNewEmailPending(null);
                        user.setEmailChangeToken(null);
                        user.setEmailChangeTokenSentAt(null);
                        return userRepository.save(user)
                                .thenReturn(new EmailChangeValidationResponse(request.getToken(), null, false));
                    }
                })
                .switchIfEmpty(Mono.just(new EmailChangeValidationResponse(request.getToken(), null, false)));
    }

    /**
     * Finalizes the email change process.
     * @param token The email change token.
     * @return A Mono that completes when the email is updated.
     */
    public Mono<Void> confirmEmailChange(String token) {
        return userRepository.findByEmailChangeToken(token)
                .flatMap(user -> {
                    if (user.getEmailChangeTokenSentAt() == null ||
                            Duration.between(user.getEmailChangeTokenSentAt(), LocalDateTime.now()).toMinutes() > TOKEN_VALIDITY_MINUTES) {
                        return Mono.error(new IllegalArgumentException("The email change token has expired."));
                    }

                    // Final email update
                    user.setEmail(user.getNewEmailPending());
                    user.setNewEmailPending(null);
                    user.setEmailChangeToken(null);
                    user.setEmailChangeTokenSentAt(null);

                    return userRepository.save(user).then();
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid email change token.")));
    }

    /**
     * Generates a random alphanumeric code of a specified length.
     *
     * @param length The desired length of the code.
     * @return The generated code string.
     */
    private String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString().toUpperCase();
    }
}