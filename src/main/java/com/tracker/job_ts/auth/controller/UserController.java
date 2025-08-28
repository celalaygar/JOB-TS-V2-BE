package com.tracker.job_ts.auth.controller;


import com.tracker.job_ts.auth.dto.ChangePasswordRequest;
import com.tracker.job_ts.auth.dto.ChangePasswordResponse;
import com.tracker.job_ts.auth.dto.RegisterRequest;
import com.tracker.job_ts.auth.dto.UserDto;
import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.auth.service.AuthService;
import com.tracker.job_ts.auth.service.UserService;
import com.tracker.job_ts.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;
    private final AuthHelperService authHelperService;


    @GetMapping("/profile")
    public Mono<UserDto> getUserProfile() {
        return authHelperService.getAuthUser().map(UserDto::new);
    }

    /**
     * Kimliği doğrulanmış kullanıcının kendi profil bilgilerini güncellemek için
     * kullanılan REST uç noktasıdır.
     * @param dto Güncellenecek verileri içeren istek gövdesi.
     * @return Güncelleme başarılı olursa 200 OK HTTP durum kodu ve güncellenmiş UserDto nesnesi döndürür.
     */
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Mono<UserDto> updateCurrentUser(@RequestBody RegisterRequest dto) {
        // Gelen DTO'yu doğrudan UserService'teki updateMe metoduna iletir.
        return userService.updateMe(dto);
    }

    /**
     * Kimliği doğrulanmış kullanıcının şifresini güncellemek için
     * kullanılan REST uç noktasıdır.
     * @param request Şifre güncelleme verilerini içeren istek gövdesi.
     * @return Güncelleme başarılı olursa Mono<Boolean> içinde 'true' döndürür.
     */
    @PatchMapping("/me/password")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        return userService.changePassword(request);
    }
}
