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
@RequestMapping(ApiPaths.EmailChangePublicCtrl.CTRL)
@RequiredArgsConstructor
public class EmailChangePublicController {

    private final EmailChangeService emailChangeService;

    /**
     * E-posta değişikliği işlemini token ile kesinleştirir.
     * @param token URL yolundaki token değeri.
     * @return Başarılı işlem için bir yanıt.
     */
    @GetMapping("/confirm/{token}")
    public Mono<ResponseEntity<EmailChangeResponse>> confirmEmailChange(@PathVariable String token) {
        return emailChangeService.confirmEmailChange(token)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                        .body(new EmailChangeResponse(false, "An error occurred: " + e.getMessage()))));
    }

    /**
     * E-posta değişikliği isteğini reddeder ve pending alanları temizler.
     * @param token URL yolundaki token değeri.
     * @return Başarılı işlem için bir yanıt.
     */
    @GetMapping("/reject/{token}")
    public Mono<ResponseEntity<EmailChangeResponse>> rejectEmailChange(@PathVariable String token) {
        return emailChangeService.rejectEmailChange(token)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                        .body(new EmailChangeResponse(false, "An error occurred: " + e.getMessage()))));
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
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(
                        new EmailChangeValidationResponse(
                                request.getToken(), null, null,false))));
    }


}