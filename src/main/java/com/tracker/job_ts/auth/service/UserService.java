package com.tracker.job_ts.auth.service;


import com.tracker.job_ts.Invitation.repository.InvitationRepository;
import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.dto.ChangePasswordRequest;
import com.tracker.job_ts.auth.dto.RegisterRequest;
import com.tracker.job_ts.auth.dto.UserDto;
import com.tracker.job_ts.auth.mapper.RegisterRequestToUserMapper;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final TokenLogService tokenLogService;
    private final RegisterRequestToUserMapper registerRequestToUserMapper;
    private final AuthHelperService authHelperService;
    private final InvitationRepository invitationRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserValidationService validationService; // ValidationService enjekte ediliyor

    /**
     * Kimliği doğrulanmış kullanıcının kendi profil bilgilerini günceller.
     * E-posta ve şifre gibi hassas bilgiler bu metod ile değiştirilemez.
     *
     * @param dto Güncellenecek kullanıcı bilgilerini içeren RegisterRequest nesnesi.
     * @return Güncellenmiş kullanıcı verilerini içeren UserDto'ya dönüştürülmüş Mono nesnesi.
     */
    public Mono<UserDto> updateMe(RegisterRequest dto) {
        return validationService.validateUpdate(dto)
                // 1. Kimliği doğrulanmış kullanıcıyı getir
                .then(authHelperService.getAuthUser())
                // 2. Kullanıcı ID'si ile tekrar veritabanından çek (isteğe bağlı güvenlik/tutarlılık adımı)
                .flatMap(authUser -> userRepository.findById(authUser.getId()))
                // 3. Kullanıcı nesnesini bulduktan sonra güncelleme işlemini gerçekleştir
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

                    // 4. Güncellenmiş kullanıcıyı kaydet
                    return userRepository.save(user);
                })
                .map(UserDto::new);
    }


    /**
     * Kimliği doğrulanmış kullanıcının şifresini günceller.
     *
     * @param request Şifre güncelleme verilerini içeren ChangePasswordRequest nesnesi.
     * @return Başarılı olursa Mono<Boolean> içinde 'true' döndürür, aksi halde hata fırlatır.
     */
    public Mono<Boolean> changePassword(ChangePasswordRequest request) {
        return Mono.just(request)
                .flatMap(req -> {
                    if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
                        return Mono.error(new IllegalArgumentException("Yeni şifreler eşleşmiyor."));
                    }
                    return Mono.empty();
                })
                // 1. Kimliği doğrulanmış kullanıcıyı getir
                .then(authHelperService.getAuthUser())
                // 2. Kullanıcı ID'si ile tekrar veritabanından çek
                .flatMap(authUser -> userRepository.findById(authUser.getId()))
                // 3. Kullanıcı nesnesini bulduktan sonra şifre değiştirme işlemini yap
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return Mono.error(new IllegalArgumentException("Mevcut şifre yanlış."));
                    }
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    user.setUpdatedAt(LocalDateTime.now());

                    // 4. Güncellenmiş kullanıcıyı kaydet
                    return userRepository.save(user);
                })
                .map(user -> true)
                .onErrorResume(e -> {
                    log.error("Şifre değiştirme hatası: {}", e.getMessage());
                    return Mono.error(e);
                });
    }
}
