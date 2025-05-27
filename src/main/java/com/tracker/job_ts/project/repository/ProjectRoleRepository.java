package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.ProjectUserRole;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectRoleRepository extends ReactiveMongoRepository<ProjectUserRole, String> {
    Flux<ProjectUserRole> findByCreatedProjectId(String projectId);
    Mono<ProjectUserRole> findByIdAndCreatedProjectId(String id, String projectId);
    Flux<ProjectUserRole> findByCreatedByIdAndCreatedProjectId(String userId, String projectId);
}