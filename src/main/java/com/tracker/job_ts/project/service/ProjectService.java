package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface  ProjectService {
    Mono<ProjectDto> create(ProjectRequestDto dto);
    Mono<ProjectDto> update(String id, ProjectDto dto);
    Mono<ProjectDto> getById(String id);
    Flux<ProjectDto> getAll();
    Mono<Void> delete(String id);
}
