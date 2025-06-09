package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.ProjectUserRole.ProjectUserRoleDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectUserRoleService {
    Mono<ProjectUserRoleDto> create(ProjectUserRoleDto dto);
    Mono<ProjectUserRoleDto> update(String id, ProjectUserRoleDto dto);
    Mono<ProjectUserRoleDto> getById(String id);
    Flux<ProjectUserRoleDto> getAllByProjectId(String projectId);
    Mono<Void> delete(String id);
}
