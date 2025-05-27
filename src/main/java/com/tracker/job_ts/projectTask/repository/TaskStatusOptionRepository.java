package com.tracker.job_ts.projectTask.repository;

import com.tracker.job_ts.projectTask.entity.TaskStatusOption;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TaskStatusOptionRepository extends ReactiveMongoRepository<TaskStatusOption, String> {
    Flux<TaskStatusOption> findByProjectTaskStatusId(String projectTaskStatusId);
}