package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectTaskStatusRepository extends ReactiveMongoRepository<ProjectTaskStatus, String> {
    Flux<ProjectTaskStatus> findByCreatedProjectId(String projectId);
    Mono<Boolean> existsByCreatedProjectIdAndName(String projectId, String name);
    Mono<Boolean> existsByCreatedProjectIdAndOrder(String projectId, Integer order);
    Flux<ProjectTaskStatus> findByCreatedProjectIdOrderByOrderDesc(String projectId);
}