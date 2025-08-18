package com.tracker.job_ts.projectTask.repository;

import com.tracker.job_ts.projectTask.entity.TaskComment;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskCommentRepository extends ReactiveMongoRepository<TaskComment, String> {
    Flux<TaskComment> findByTaskIdOrderByCreatedAtDesc(String taskId);
    Mono<TaskComment> findByIdAndTaskId(String id, String taskId);
}