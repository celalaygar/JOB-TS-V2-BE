package com.tracker.job_ts.auth.config;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JWTProvider jwtProvider;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String email = jwtProvider.getUsernameFromToken(authToken);

        return Mono.just(jwtProvider.validateToken(authToken))
                .filter(valid -> valid)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid token")))
                .map(valid -> {
                    //List<String> roles = jwtProvider.getClaims(authToken).get("roles", List.class);
                    return new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            jwtProvider.getAuthorities(authToken)
                    );
                });
    }
}