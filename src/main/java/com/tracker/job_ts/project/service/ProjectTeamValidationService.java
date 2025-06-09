package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamDto;
import com.tracker.job_ts.project.exception.projectTeam.ProjectTeamValidationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProjectTeamValidationService {
    public Mono<Void> validate(ProjectTeamDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            return Mono.error(new ProjectTeamValidationException("Name is required"));
        }
        return Mono.empty();
    }
}