package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.ProjectTeam;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectTeamRepository extends ReactiveMongoRepository<ProjectTeam, String> {

    Flux<ProjectTeam> findByCreatedProjectId(String projectId);
    Mono<Boolean> existsByCreatedProjectIdAndName(String projectId, String name);
    Mono<ProjectTeam> findByIdAndCreatedProjectId(String id, String projectId);
}