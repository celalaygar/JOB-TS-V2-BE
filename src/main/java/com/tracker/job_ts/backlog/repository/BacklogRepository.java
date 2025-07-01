package com.tracker.job_ts.backlog.repository;

import com.tracker.job_ts.backlog.entity.Backlog;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface BacklogRepository extends ReactiveMongoRepository<Backlog, String> {
    Mono<Backlog> findByCreatedProjectId(String projectId);
}
