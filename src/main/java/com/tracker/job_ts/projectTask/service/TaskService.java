package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.projectTask.entity.ProjectTask;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskService {
    Mono<ProjectTask> createTask(ProjectTask task);
    Mono<ProjectTask> getTaskById(String id);
    Flux<ProjectTask> getAllTasks();
    Mono<ProjectTask> updateTask(String id, ProjectTask task);
    Mono<Void> deleteTask(String id);
}
