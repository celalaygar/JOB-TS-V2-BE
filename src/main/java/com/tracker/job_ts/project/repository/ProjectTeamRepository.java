package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTeams;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectTeamRepository extends ReactiveMongoRepository<ProjectTeams, String> {

    Flux<ProjectTeams> findByCreatedProjectId(String projectId);
    Mono<Boolean> existsByCreatedProjectIdAndName(String projectId, String name);
    Mono<ProjectTeams> findByIdAndCreatedProjectId(String id, String projectId);
}