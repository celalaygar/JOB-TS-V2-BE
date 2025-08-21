package com.tracker.job_ts.auth.service;


import jakarta.validation.ValidationException;
import org.springframework.stereotype.Service;

import com.tracker.job_ts.auth.dto.RegisterRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Pattern;

@Service
public class UserValidationService {
    // Telefon numarası formatını kontrol etmek için basit bir regex
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\s-]{7,15}$");

    /**
     * Güncelleme isteği için gelen RegisterRequest nesnesini doğrular.
     * Bu metod, e-posta ve şifre alanlarının güncellenmesini engeller.
     *
     * @param dto Doğrulanacak olan RegisterRequest nesnesi.
     * @return Eğer doğrulama başarılı olursa boş bir Mono<Void> döndürür,
     * aksi halde bir ValidationException hatası fırlatır.
     */
    public Mono<Void> validate(RegisterRequest dto) {
        // Mono.just(dto) ile reaktif akışı başlatır ve doğrulama mantığını
        // bir Mono içinde uygularız.
        return Mono.just(dto)
                .flatMap(request -> {
                    if (StringUtils.hasText(request.getEmail())) {
                        return Mono.error(new ValidationException("E-posta adresi bu servis ile güncellenemez."));
                    }

                    if (StringUtils.hasText(request.getPassword())) {
                        return Mono.error(new ValidationException("Şifre bu servis ile güncellenemez."));
                    }

                    if (StringUtils.hasText(request.getPhone()) && !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
                        return Mono.error(new ValidationException("Geçersiz telefon numarası formatı."));
                    }

                    if (request.getDateOfBirth() != null) {
                        LocalDate birthDate = request.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (birthDate.isAfter(LocalDate.now())) {
                            return Mono.error(new ValidationException("Doğum tarihi gelecekte olamaz."));
                        }
                    }

                    // Başarılı doğrulama durumunda, boş bir Mono döndürür.
                    return Mono.empty();
                });
    }
}