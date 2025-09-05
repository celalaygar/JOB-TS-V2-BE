package com.tracker.job_ts.auth.config;


import com.tracker.job_ts.auth.exception.IllegalArgumentTokenException;
import com.tracker.job_ts.auth.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationManager /*implements ReactiveAuthenticationManager */{

//    private final JWTProvider jwtProvider;
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        String token = authentication.getCredentials().toString();
//
//        if (token == null || token.isBlank()) {
//            return Mono.error(new IllegalArgumentTokenException("Token is missing"));
//        }
//
//        try {
//            jwtProvider.validateToken(token);
//            String email = jwtProvider.getUsernameFromToken(token);
//            List<SimpleGrantedAuthority> authorities = jwtProvider.getAuthorities(token);
//            return Mono.just(new UsernamePasswordAuthenticationToken(email, null, authorities));
//        } catch (Exception e) {
//            // Sadece AuthenticationException ve alt sınıflarını direkt geç
//            if (e instanceof AuthenticationException) {
//                return Mono.error(e);
//            }
//            return Mono.error(new UnauthorizedException("Invalid token", e));
//        }
//    }
}