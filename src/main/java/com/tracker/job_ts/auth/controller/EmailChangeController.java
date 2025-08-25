package com.tracker.job_ts.auth.controller;

import com.tracker.job_ts.auth.dto.EmailChangeRequest;
import com.tracker.job_ts.auth.dto.EmailChangeResponse;
import com.tracker.job_ts.auth.dto.EmailChangeValidationRequest;
import com.tracker.job_ts.auth.dto.EmailChangeValidationResponse;
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


    /**
     * Token'ı doğrular ve e-posta değişikliği için gerekli bilgileri döndürür.
     * Bu metot, e-posta değişikliğini kesinleştirmez.
     * @param request İstek gövdesinde token'ı içerir.
     * @return Token'ın geçerliliğine dair bir yanıt nesnesi.
     */
    @PostMapping("/validate-token")
    public Mono<ResponseEntity<EmailChangeValidationResponse>> validateToken(@RequestBody EmailChangeValidationRequest request) {
        return emailChangeService.validateToken(request)
                .map(response -> ResponseEntity.ok(response))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(new EmailChangeValidationResponse(request.getToken(), null, false))));
    }

    /**
     * E-posta değişikliği işlemini token ile kesinleştirir.
     * @param token URL yolundaki token değeri.
     * @return Başarılı işlem için bir yanıt.
     */
    @PostMapping("/confirm/{token}")
    public Mono<ResponseEntity<String>> confirmEmailChange(@PathVariable String token) {
        return emailChangeService.confirmEmailChange(token)
                .then(Mono.just(ResponseEntity.ok("Email has been successfully updated.")))
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException) {
                        return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
                    } else {
                        return Mono.just(ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage()));
                    }
                });
    }

}