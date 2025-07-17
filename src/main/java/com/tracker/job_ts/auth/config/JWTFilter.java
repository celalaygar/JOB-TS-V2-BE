package com.tracker.job_ts.auth.config;

import com.tracker.job_ts.auth.exception.*;
import com.tracker.job_ts.logging.service.RequestLoggingService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Component
@Order(1)
@RequiredArgsConstructor
public class JWTFilter implements WebFilter {

    private final JWTProvider jwtProvider;
    private final RequestLoggingService requestLoggingService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString();
        String token = getJwtFromRequest(exchange);
        String clientIp = resolveClientIp(exchange);

        // Request body'yi alıyoruz
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .defaultIfEmpty(exchange.getResponse().bufferFactory().wrap(new byte[0]))
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    String requestBody = new String(bytes, StandardCharsets.UTF_8);

                    // Cached request body
                    var cachedRequest = new CachedBodyServerHttpRequest(
                            exchange.getRequest(), bytes, exchange.getResponse().bufferFactory());

                    AtomicReference<String> responseBodyRef = new AtomicReference<>();

                    // Response body'yi yakalamak için decorator
                    var decoratedResponse = new ServerHttpResponseDecoratorWithBody(
                            exchange.getResponse(), responseBodyRef::set);

                    // Exchange'yi dekoratörlerle mutasyona uğratıyoruz
                    ServerWebExchange decoratedExchange = exchange.mutate()
                            .request(cachedRequest)
                            .response(decoratedResponse)
                            .build();

                    // Response loglaması için asıl işlemi chain'e ekliyoruz
                    Mono<Void> result;

                    if (token != null && !token.isEmpty()) {
                        try {
                            // JWT doğrulama işlemi
                            if (jwtProvider.validateToken(token)) {
                                String username = jwtProvider.getUsernameFromToken(token);
                                var authentication = new UsernamePasswordAuthenticationToken(
                                        username, null, jwtProvider.getAuthorities(token));

                                // JWT geçerli ise filtreyi çalıştırıyoruz
                                result = chain.filter(decoratedExchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                                        .then(Mono.defer(() -> {
                                            String capturedResponseBody = responseBodyRef.get();
                                            return requestLoggingService.logRequestFull(
                                                    cachedRequest,
                                                    decoratedResponse,
                                                    requestBody,
                                                    capturedResponseBody,
                                                    traceId,
                                                    startTime,
                                                    clientIp,
                                                    username,
                                                    false
                                            );
                                        }));
                            } else {
                                return unauthorized(exchange, new UnauthorizedException("Invalid JWT"));
                            }
                        } catch (Exception e) {
                            return unauthorized(exchange, e);
                        }
                    } else {
                        // Token yoksa kullanıcı bilgisi null olacak şekilde loglanacak
                        result = chain.filter(decoratedExchange)
                                .then(Mono.defer(() -> {
                                    String capturedResponseBody = responseBodyRef.get();
                                    return requestLoggingService.logRequestFull(
                                            cachedRequest,
                                            decoratedResponse,
                                            requestBody,
                                            capturedResponseBody,
                                            traceId,
                                            startTime,
                                            clientIp,
                                            null,
                                            false
                                    );
                                }));
                    }

                    return result;
                });
    }

    private String getJwtFromRequest(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst("Authorization");
    }

    private String resolveClientIp(ServerWebExchange exchange) {
        String clientIp = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (clientIp == null && exchange.getRequest().getRemoteAddress() != null) {
            clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return clientIp;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, Exception ex) {
        HttpStatus status;
        if (ex instanceof ExpiredJwtException || ex instanceof UnauthorizedException) {
            status = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof MalformedJwtException || ex instanceof UnsupportedJwtException) {
            status = HttpStatus.BAD_REQUEST;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        byte[] bytes = ("{\"error\":\"" + ex.getMessage() + "\"}").getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
