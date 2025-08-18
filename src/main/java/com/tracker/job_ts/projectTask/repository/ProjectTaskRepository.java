package com.tracker.job_ts.projectTask.repository;

import com.tracker.job_ts.projectTask.entity.ProjectTask;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProjectTaskRepository extends ReactiveMongoRepository<ProjectTask, String> {
    Flux<ProjectTask> findByAssigneeId(int assigneeId);
    Flux<ProjectTask> findByCreatedProjectId(String projectId);
    Flux<ProjectTask> findByParentTaskId(String parentTaskId);
    Flux<ProjectTask> findBySprintIdAndCreatedProjectId(String sprintId, String projectId);
    Flux<ProjectTask> findBySprintId(String sprintId);
    Mono<ProjectTask> findByIdAndSprintIdAndCreatedProjectId(String taskId, String sprintId, String projectId);
    Flux<ProjectTask> findByCreatedProjectIdAndSprintIsNull(String projectId);
}