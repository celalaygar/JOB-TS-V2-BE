package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectUserRepository extends ReactiveMongoRepository<ProjectUser, String> {
    Flux<ProjectUser> findByProjectId(String projectId);
    Flux<ProjectUser> findByProjectIdAndProjectSystemRole(String projectId, ProjectSystemRole projectSystemRole);

    Mono<ProjectUser> findByProjectIdAndUserId(String projectId, String userId);
    Mono<ProjectUser> findByIdAndProjectId(String id, String projectId);
    Flux<ProjectUser> findAllByUserId(String userId);
    Mono<ProjectUser> findByProjectIdAndEmail(String projectId, String email);
    // Yeni eklenen metot
    Flux<ProjectUser> findByProjectIdAndProjectTeamIdsContaining(String projectId, String teamId);

    Flux<ProjectUser> findByProjectIdAndProjectTeamIdsNotContaining(String projectId, String teamId);

}
