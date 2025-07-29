package com.tracker.job_ts.project.repository;

import com.tracker.job_ts.project.entity.ProjectUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectUserRepository extends ReactiveMongoRepository<ProjectUser, String> {
    Flux<ProjectUser> findByProjectId(String projectId);
    Mono<ProjectUser> findByProjectIdAndUserId(String projectId, String userId);
    Mono<ProjectUser> findByIdAndProjectId(String id, String projectId);
    Flux<ProjectUser> findAllByUserId(String userId);
    Mono<ProjectUser> findByProjectIdAndEmail(String projectId, String email);
    // Yeni eklenen metot
    Flux<ProjectUser> findByProjectIdAndProjectTeamIdsContaining(String projectId, String teamId);

    // Yeni eklenecek metod: Belirli bir projede olan ama belirli bir takımda olmayan kullanıcıları bulur.
    // MongoDB'de $nin (not in) operatörü kullanılır.
    // findByProjectId ve projectTeamIds field'ında teamId'si OLMAYANları filtrele.
    Flux<ProjectUser> findByProjectIdAndProjectTeamIdsNotContaining(String projectId, String teamId);

}
