package com.tracker.job_ts.sprint.repository;

import com.tracker.job_ts.sprint.entity.SprintUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SprintUserRepository extends ReactiveMongoRepository<SprintUser, String> {
    Flux<SprintUser> findBySprintId(String sprintId);
    Flux<SprintUser> findAllByUserId(String userId);

    Mono<Void> deleteBySprintIdAndUserId(String sprintId, String userId);
    Mono<Boolean> existsBySprintIdAndUserId(String sprintId, String userId);
}
