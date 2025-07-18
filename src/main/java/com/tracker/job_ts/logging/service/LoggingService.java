package com.tracker.job_ts.logging.service;

import com.tracker.job_ts.logging.entity.ApiLog;
import com.tracker.job_ts.logging.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LoggingService {

    private final ApiLogRepository apiLogRepository;

    public Mono<ApiLog> saveApiLog(ApiLog apiLog) {
        return apiLogRepository.save(apiLog)
                .doOnSuccess(savedLog -> System.out.println("API Log kaydedildi: " + savedLog.getTraceId()))
                .doOnError(e -> System.err.println("API Log kaydederken hata oluştu: " + e.getMessage()));
    }
}