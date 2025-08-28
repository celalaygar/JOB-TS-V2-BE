package com.tracker.job_ts.auth.controller;

import com.tracker.job_ts.auth.dto.emailChange.EmailChangeRequest;
import com.tracker.job_ts.auth.dto.emailChange.EmailChangeResponse;
import com.tracker.job_ts.auth.service.EmailChangeService;
import com.tracker.job_ts.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.EmailChangeCtrl.CTRL)
@RequiredArgsConstructor
public class EmailChangeController {

    private final EmailChangeService emailChangeService;

    /**
     * Endpoint to send a verification code to a user's email for an email change process.
     *
     * @param username The username of the user.
     * @return A Mono of ResponseEntity indicating the status of the operation.
     */
    @GetMapping("/send-code")
    public Mono<ResponseEntity<EmailChangeResponse>> sendChangeCode() {
        return emailChangeService.sendChangeCode()
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().body(new EmailChangeResponse(false, e.getMessage())));
                    } else if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(429).body(new EmailChangeResponse(false, e.getMessage())));
                    } else {
                        return Mono.just(ResponseEntity.internalServerError().body(new EmailChangeResponse(false, "An error occurred: " + e.getMessage())));
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
    public Mono<ResponseEntity<EmailChangeResponse>> verifyAndSendLink(@RequestBody EmailChangeRequest request) {
        return emailChangeService.verifyAndGenerateChangeLink(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().body(new EmailChangeResponse(false, e.getMessage())));
                    } else if (e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.status(429).body(new EmailChangeResponse(false, e.getMessage())));
                    } else {
                        return Mono.just(ResponseEntity.internalServerError().body(new EmailChangeResponse(false, "An error occurred: " + e.getMessage())));
                    }
                });
    }




}