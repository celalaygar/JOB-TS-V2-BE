// com.tracker.job_ts.logging.repository.RequestLogRepository.java
package com.tracker.job_ts.logging.repository;

import com.tracker.job_ts.logging.entity.RequestLog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository extends ReactiveMongoRepository<RequestLog, String> {
}