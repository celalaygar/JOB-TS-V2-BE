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
                        return Mono.error(new ValidationException("Email address cannot be updated with this service."));
                    }

                    if (StringUtils.hasText(request.getPassword())) {
                        return Mono.error(new ValidationException("Password cannot be updated with this service."));
                    }

                    if (StringUtils.hasText(request.getPhone()) && !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
                        return Mono.error(new ValidationException("Invalid phone number format."));
                    }

                    if (request.getDateOfBirth() != null) {
                        LocalDate birthDate = request.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (birthDate.isAfter(LocalDate.now())) {
                            return Mono.error(new ValidationException("The date of birth cannot be in the future."));
                        }
                    }

                    // Başarılı doğrulama durumunda, boş bir Mono döndürür.
                    return Mono.empty();
                });
    }

    /**
     * Validates the fields of a RegisterRequest object for user profile updates.
     * This method checks for valid data in fields other than email and password.
     *
     * @param dto The RegisterRequest object to validate.
     * @return An empty Mono<Void> if validation is successful, otherwise throws a ValidationException.
     */
    public Mono<Void> validateUpdate(RegisterRequest dto) {
        return Mono.just(dto)
                .flatMap(request -> {
                    // Username validation
                    if (StringUtils.hasText(request.getUsername()) && request.getUsername().length() < 3) {
                        return Mono.error(new ValidationException("Username must be at least 3 characters long."));
                    }

                    // Firstname validation
                    if (StringUtils.hasText(request.getFirstname()) && request.getFirstname().length() < 2) {
                        return Mono.error(new ValidationException("Firstname must be at least 2 characters long."));
                    }

                    // Lastname validation
                    if (StringUtils.hasText(request.getLastname()) && request.getLastname().length() < 2) {
                        return Mono.error(new ValidationException("Lastname must be at least 2 characters long."));
                    }

                    // Phone number validation
                    if (StringUtils.hasText(request.getPhone()) && !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
                        return Mono.error(new ValidationException("Invalid phone number format."));
                    }

                    // Date of Birth validation
                    if (request.getDateOfBirth() != null) {
                        LocalDate birthDate = request.getDateOfBirth().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        if (birthDate.isAfter(LocalDate.now())) {
                            return Mono.error(new ValidationException("Date of birth cannot be in the future."));
                        }
                    }

                    // If all checks pass, return an empty Mono
                    return Mono.empty();
                });
    }
}