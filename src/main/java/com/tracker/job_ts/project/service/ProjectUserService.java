package com.tracker.job_ts.project.service;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProjectUserService {
    Flux<User> listProjectUsers(String projectId);
    Mono<Void> addUserToProject(ProjectUserDTO dto);
    Mono<Void> removeUserFromProject(ProjectUserDTO dto);
}