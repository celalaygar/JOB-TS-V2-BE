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
     * @param dto Güncellenecek kullanıcı bilgilerini içeren RegisterRequest nesnesi.
     * @return Güncellenmiş kullanıcı verilerini içeren UserDto'ya dönüştürülmüş Mono nesnesi.
     */
    public Mono<UserDto> updateMe(RegisterRequest dto) {
        // Gelen DTO'yu ValidationService ile doğrular.
        return validationService.validate(dto)
                // Kimliği doğrulanmış kullanıcının kendisini getirir.
                .then(authHelperService.getAuthUser())
                // Kullanıcı nesnesi bulunduğunda, güncelleme işlemini gerçekleştirir.
                .flatMap(user -> {
                    // Yalnızca güncellenebilecek alanları DTO'dan alıp User nesnesine set eder.
                    // E-posta ve şifre güncellenmez.
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
                    if (dto.getGender() != null) {
                        user.setGender(dto.getGender());
                    }
                    if (dto.getDepartment() != null) {
                        user.setDepartment(dto.getDepartment());
                    }

                    user.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(user);
                })
                .map(UserDto::new);
    }


    /**
     * Kimliği doğrulanmış kullanıcının şifresini günceller.
     * @param request Şifre güncelleme verilerini içeren ChangePasswordRequest nesnesi.
     * @return Başarılı olursa boş bir Mono<Void> döndürür, aksi halde hata fırlatır.
     */
    public Mono<Void> changePassword(ChangePasswordRequest request) {
        return Mono.just(request)
                .flatMap(req -> {
                    // Yeni şifre ve tekrarının eşleşip eşleşmediğini kontrol eder.
                    if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
                        return Mono.error(new IllegalArgumentException("Yeni şifreler eşleşmiyor."));
                    }
                    // Diğer validasyon kuralları burada eklenebilir (örneğin, şifre uzunluğu).
                    return Mono.empty();
                })
                // Kimliği doğrulanmış kullanıcının kendisini getirir.
                .then(authHelperService.getAuthUser())
                .flatMap(user -> {
                    // Mevcut şifreyi doğrular.
                    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                        return Mono.error(new IllegalArgumentException("Mevcut şifre yanlış."));
                    }
                    // Yeni şifreyi hash'leyerek kullanıcı nesnesine set eder.
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    user.setUpdatedAt(LocalDateTime.now());
                    // Güncellenmiş kullanıcıyı veritabanına kaydeder.
                    return userRepository.save(user);
                })
                .then(); // Sadece başarılı bir durumun sinyalini göndermek için
    }
}
