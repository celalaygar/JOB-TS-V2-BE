package com.tracker.job_ts.projectTask.repository;

import com.tracker.job_ts.projectTask.entity.ProjectTask;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProjectTaskRepository extends ReactiveMongoRepository<ProjectTask, String> {
    Flux<ProjectTask> findByAssigneeId(int assigneeId);
    Flux<ProjectTask> findByCreatedProjectId(String projectId);
}