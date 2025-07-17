
package com.tracker.job_ts.logging.service;


import com.tracker.job_ts.logging.entity.RequestLog;
import com.tracker.job_ts.logging.repository.RequestLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestLoggingService {

    private final RequestLogRepository requestLogRepository;

    public Mono<Void> logRequest(ServerHttpRequest request,
                                 ServerHttpResponse response,
                                 String requestBody,
                                 String traceId,
                                 long startTime,
                                 String clientIp,
                                 String username,
                                 boolean isError) {

        long duration = System.currentTimeMillis() - startTime;

        Map<String, String> headers = new HashMap<>();
        request.getHeaders().forEach((k, v) -> headers.put(k, String.join(";", v)));

        Map<String, String> queryParams = new HashMap<>();
        request.getQueryParams().forEach((k, v) -> queryParams.put(k, String.join(";", v)));

        RequestLog log = RequestLog.builder()
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .clientIp(clientIp)
                .username(username)
                .userAgent(request.getHeaders().getFirst("User-Agent"))
                .requestMethod(request.getMethod().name())
                .requestUri(request.getURI().toString())
                .requestPath(request.getPath().value())
                .requestQueryParams(queryParams)
                .requestHeaders(headers)
                .requestBody(requestBody)
                .responseStatus(response.getStatusCode() != null ? response.getStatusCode().value() : null)
                .responseTimeMillis(duration)
                .isError(isError)
                .build();

        return requestLogRepository.save(log).then();
    }
    public Mono<Void> logRequestFull(ServerHttpRequest request,
                                     ServerHttpResponse response,
                                     String requestBody,
                                     String responseBody,
                                     String traceId,
                                     long startTime,
                                     String clientIp,
                                     String username,
                                     boolean isError) {

        long duration = System.currentTimeMillis() - startTime;

        Map<String, String> headers = new HashMap<>();
        request.getHeaders().forEach((k, v) -> headers.put(k, String.join(";", v)));

        Map<String, String> queryParams = new HashMap<>();
        request.getQueryParams().forEach((k, v) -> queryParams.put(k, String.join(";", v)));

        Map<String, String> responseHeaders = new HashMap<>();
        response.getHeaders().forEach((k, v) -> responseHeaders.put(k, String.join(";", v)));

        RequestLog log = RequestLog.builder()
                .timestamp(LocalDateTime.now())
                .traceId(traceId)
                .clientIp(clientIp)
                .username(username)
                .userAgent(request.getHeaders().getFirst("User-Agent"))
                .requestMethod(request.getMethod().name())
                .requestUri(request.getURI().toString())
                .requestPath(request.getPath().value())
                .requestQueryParams(queryParams)
                .requestHeaders(headers)
                .requestBody(requestBody)
                .responseBody(responseBody)
                .responseStatus(response.getStatusCode() != null ? response.getStatusCode().value() : null)
                .responseHeaders(responseHeaders)
                .responseTimeMillis(duration)
                .isError(isError)
                .build();

        return requestLogRepository.save(log).then();
    }
}
