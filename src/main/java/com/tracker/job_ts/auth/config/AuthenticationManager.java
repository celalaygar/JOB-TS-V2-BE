package com.tracker.job_ts.auth.config;


import com.tracker.job_ts.auth.exception.ExpiredJwtTokenException;
import com.tracker.job_ts.auth.exception.JwtAuthenticationException;
import com.tracker.job_ts.auth.exception.MalformedJwtTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        if (token == null || !token.startsWith("Bearer ")) {
            return Mono.empty();
        }

        String authToken = token.substring(7);

        return Mono.fromCallable(() -> {
                    // Bu metod hata fırlatırsa, onErrorMap ile yakalanır
                    jwtProvider.validateToken(authToken);
                    String email = jwtProvider.getUsernameFromToken(authToken);
                    return new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            jwtProvider.getAuthorities(authToken)
                    );
                })
                .onErrorResume(ExpiredJwtTokenException.class, ex ->
                        Mono.error(new JwtAuthenticationException("Token expired","EXPIRED"))
                )
                .onErrorResume(MalformedJwtTokenException.class, ex ->
                        Mono.error(new JwtAuthenticationException("Malformed token","MAL_FORMED"))
                )
                .onErrorResume(IllegalArgumentException.class, ex ->
                        Mono.error(new JwtAuthenticationException("Invalid token","INVALID"))
                )
                .onErrorResume(Exception.class, ex ->
                        Mono.error(new JwtAuthenticationException("Authentication failed","UNKNOWN"))
                )
                .cast(Authentication.class)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
