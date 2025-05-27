package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.projectTask.dto.TaskStatusOptionDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TaskStatusOptionService {
    Mono<TaskStatusOptionDTO> create(TaskStatusOptionDTO dto);
    Mono<TaskStatusOptionDTO> update(String id, TaskStatusOptionDTO dto);
    Mono<TaskStatusOptionDTO> getById(String id);
    Flux<TaskStatusOptionDTO> getByProjectTaskStatusId(String projectTaskStatusId);
    Mono<Void> delete(String id);
}
