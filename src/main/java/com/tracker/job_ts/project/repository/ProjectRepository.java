package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectRepository extends ReactiveMongoRepository<Project, String> {
    Flux<Project> findByStatus(String status);
    Flux<Project> findByPriority(String priority);
    Flux<Project> findAllByCreatedByUserId(String userId);
    Flux<Project> findAllByCreatedByUserIdAndProjectSystemStatus(String userId, ProjectSystemStatus projectSystemStatus);
    Mono<Project> findByIdAndCreatedByUserId(String id, String userId);
    Mono<Project> findByIdAndCreatedByUserIdAndProjectSystemStatus(String id, String userId, ProjectSystemStatus projectSystemStatus);
}