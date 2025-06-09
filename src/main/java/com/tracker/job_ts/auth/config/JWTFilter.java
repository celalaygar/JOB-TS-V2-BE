package com.tracker.job_ts.auth.config;

import com.tracker.job_ts.auth.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class JWTFilter implements WebFilter {

    private final JWTProvider jwtProvider;

    public JWTFilter(JWTProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = getJwtFromRequest(exchange);

        if (token != null && !"".equals(token)) {
            try {
                if (jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsernameFromToken(token);
                    var authentication = new UsernamePasswordAuthenticationToken(
                            username, null, jwtProvider.getAuthorities(token));

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                }
            } catch (ExpiredJwtTokenException e) {
                return unauthorized(exchange, e);
            } catch (UnauthorizedException e) {
                return unauthorized(exchange, e);
            } catch (MalformedJwtTokenException | UnsupportedJwtTokenException |
                     IllegalArgumentTokenException | SecurityJwtTokenException e) {
                // Diğer JWT hatalarında da 400 Bad Request veya 401 dönmek isterseniz, burayı özelleştirin
                return unauthorized(exchange, e);
            } catch (Exception e) {
                // Öngörülmeyen hatalar için (opsiyonel olarak 500 de olabilir)
                return unauthorized(exchange, new UnauthorizedException("Token doğrulama sırasında hata."));
            }
        }

        return chain.filter(exchange);
    }
    private Mono<Void> unauthorized(ServerWebExchange exchange, RuntimeException ex) {
        HttpStatus status;

        if (ex instanceof ExpiredJwtTokenException || ex instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED; // 401
        } else if (ex instanceof MalformedJwtTokenException ||
                ex instanceof UnsupportedJwtTokenException ||
                ex instanceof IllegalArgumentTokenException ||
                ex instanceof SecurityJwtTokenException) {
            status = HttpStatus.BAD_REQUEST; // 400
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500 - Öngörülmeyen hata
        }

        byte[] bytes = ("{\"error\":\"" + ex.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().setContentLength(bytes.length);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }


    private String getJwtFromRequest(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("Authorization");
    }
}

