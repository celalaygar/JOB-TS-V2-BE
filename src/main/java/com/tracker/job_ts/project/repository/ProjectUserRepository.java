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

    Mono<ProjectUser> findByProjectIdAndUserIdAndProjectSystemRole(String projectId, String userId, ProjectSystemRole projectSystemRole);
    Mono<ProjectUser> findByProjectIdAndUserId(String projectId, String userId);
    Mono<ProjectUser> findByIdAndProjectId(String id, String projectId);
    Mono<ProjectUser> findByIdAndProjectIdAndProjectSystemRoleNot(String id, String projectId, ProjectSystemRole projectSystemRole);
    Mono<ProjectUser> findByIdAndProjectIdAndProjectSystemRole(String id, String projectId, ProjectSystemRole projectSystemRole);
    Flux<ProjectUser> findAllByUserId(String userId);
    Flux<ProjectUser> findAllByUserIdAndProjectSystemRoleNot(String userId, ProjectSystemRole projectSystemRole);
    Mono<ProjectUser> findByProjectIdAndEmail(String projectId, String email);
    // Belirli bir projede belirli bir takım ID'sine sahip kullanıcıları bulma
    Flux<ProjectUser> findByProjectIdAndProjectTeamIdsContaining(String projectId, String teamId);

    // Belirli bir projede belirli bir takım ID'sine sahip olmayan kullanıcıları bulma
    Flux<ProjectUser> findByProjectIdAndProjectTeamIdsNotContaining(String projectId, String teamId);

    // YENİ EKLENEN METOT: Belirli bir role sahip olmayan kullanıcıyı bulma
    Mono<ProjectUser> findByProjectIdAndUserIdAndProjectSystemRoleNot(String projectId, String userId, ProjectSystemRole projectSystemRole);
}