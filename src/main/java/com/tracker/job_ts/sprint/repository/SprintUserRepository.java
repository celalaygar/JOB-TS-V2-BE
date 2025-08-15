package com.tracker.job_ts.sprint.repository;

import com.tracker.job_ts.sprint.entity.SprintUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface SprintUserRepository extends ReactiveMongoRepository<SprintUser, String> {
    Flux<SprintUser> findBySprintId(String sprintId);
    Mono<SprintUser> findBySprintIdAndCreatedById(String sprintId, String userId);
    Flux<SprintUser> findByCreatedById( String userId); // Bu metodun adını findByUserId olarak düzeltmek daha mantıklı olabilir.
    Flux<SprintUser> findByCreatedProjectId(String projectId); // createdProject.id alanı üzerinden arama yapacaktır.
    Flux<SprintUser> findBySprintIdAndProjectId(String sprintId, String projectId);
}
