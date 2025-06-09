package com.tracker.job_ts.projectTask.repository;

import com.tracker.job_ts.projectTask.entity.ProjectTask;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TaskRepository extends ReactiveMongoRepository<ProjectTask, String> {
    Flux<ProjectTask> findByProjectId(String projectId);
    Flux<ProjectTask> findByAssignee_Id(int assigneeId);
}