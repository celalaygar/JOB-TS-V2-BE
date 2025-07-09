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

    // Sprint ID'sine ve Proje ID'sine göre görevleri bulma
    Flux<ProjectTask> findBySprintIdAndCreatedProjectId(String sprintId, String projectId);

    // Sadece sprint ID'sine göre görevleri bulma (AssaignSprint modelinin id alanı)
    Flux<ProjectTask> findBySprintId(String sprintId);

    // Belirli bir sprint ID'sine ve belirli bir proje ID'sine ait belirli bir görevi bulma
    Mono<ProjectTask> findByIdAndSprintIdAndCreatedProjectId(String taskId, String sprintId, String projectId);

    // Belirli bir proje ID'sine sahip ancak AssaignSprint alanı null olan görevleri bulma (Backlog'daki görevler)
    Flux<ProjectTask> findByCreatedProjectIdAndSprintIsNull(String projectId);

}