package com.tracker.job_ts.auth.controller;


import com.tracker.job_ts.auth.dto.passwordReset.*;
import com.tracker.job_ts.auth.service.PasswordResetService;
import com.tracker.job_ts.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.PasswordResetPublicCtrl.CTRL)
@RequiredArgsConstructor
public class PasswordResetPublicController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/forgot-password")
    public Mono<ResponseEntity<PasswordResetResponse>> forgotPassword(
            @RequestBody PasswordResetRequest request) {
        return passwordResetService.sendResetMail(request.getEmail())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/validate-reset-token")
    public Mono<ResponseEntity<PasswordResetValidateResponse>> validateResetToken(
            @RequestBody PasswordResetValidateRequest request) {
        return passwordResetService.validateToken(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/reset-password")
    public Mono<ResponseEntity<PasswordResetResponse>> resetPassword(
            @RequestBody PasswordResetConfirmRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(new PasswordResetResponse(false, "Passwords do not match.")));
        }

        return passwordResetService.resetPassword(request.getToken(), request.getNewPassword())
                .map(ResponseEntity::ok);
    }
}
