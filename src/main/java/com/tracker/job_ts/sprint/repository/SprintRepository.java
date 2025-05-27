package com.tracker.job_ts.sprint.repository;

import com.tracker.job_ts.sprint.entity.Sprint;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SprintRepository extends ReactiveMongoRepository<Sprint, String> {
    Flux<Sprint> findByStatus(String status);
}