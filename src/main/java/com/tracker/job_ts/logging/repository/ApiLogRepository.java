package com.tracker.job_ts.logging.repository;


import com.tracker.job_ts.logging.entity.ApiLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ApiLogRepository extends ReactiveMongoRepository<ApiLog, String> {
    // Özel sorgular gerekirse buraya eklenebilir
}