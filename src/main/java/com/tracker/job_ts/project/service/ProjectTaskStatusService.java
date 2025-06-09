package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusDTO;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectTaskStatusService {
    Mono<ProjectTaskStatusResponseDTO> create(ProjectTaskStatusDTO dto);
    Mono<ProjectTaskStatusResponseDTO> update(String id, ProjectTaskStatusDTO dto);
    Mono<ProjectTaskStatusResponseDTO> getById(String id);
    Flux<ProjectTaskStatusResponseDTO> getByProjectId(String projectId);
    Mono<Void> delete(String id);
}