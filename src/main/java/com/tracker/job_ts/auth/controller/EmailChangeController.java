package com.tracker.job_ts.auth.controller;

import com.tracker.job_ts.auth.dto.EmailChangeRequest;
import com.tracker.job_ts.auth.service.EmailChangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/email-change")
@RequiredArgsConstructor
public class EmailChangeController {

    private final EmailChangeService emailChangeService;

    /**
     * Endpoint to send a verification code to a user's email for an email change process.
     *
     * @param username The username of the user.
     * @return A Mono of ResponseEntity indicating the status of the operation.
     */
    @PostMapping("/send-code")
    public Mono<ResponseEntity<String>> sendChangeCode() {
        return emailChangeService.sendChangeCode()
                .map(aVoid -> ResponseEntity.ok("Verification code sent successfully. Check your email."))
                .onErrorResume(e -> {
                    // Handle specific exceptions and return appropriate HTTP status codes
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                    } else if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(429).body(e.getMessage())); // 429 Too Many Requests
                    } else {
                        return Mono.just(ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage()));
                    }
                });
    }
    /**
     * Endpoint to verify password and code, and send a confirmation link to the new email address.
     *
     * @param request The request body containing the current password, new email, and verification code.
     * @return A Mono of ResponseEntity indicating the status of the operation.
     */
    @PostMapping("/verify-and-send-link")
    public Mono<ResponseEntity<String>> verifyAndSendLink(@RequestBody EmailChangeRequest request) {
        return emailChangeService.verifyAndGenerateChangeLink(request)
                .then(Mono.just(ResponseEntity.ok("Verification link sent to your new email address.")))
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                    } else if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(429).body(e.getMessage()));
                    } else {
                        return Mono.just(ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage()));
                    }
                });
    }
}