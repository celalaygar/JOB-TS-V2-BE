package com.tracker.job_ts.project.service;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectUserService {
    Flux<ProjectUserResponseDto> listProjectUsers(String projectId);
    Mono<Void> addUserToProject(ProjectUserDTO dto);
    Mono<Void> removeUserFromProject(ProjectUserDTO dto);
}