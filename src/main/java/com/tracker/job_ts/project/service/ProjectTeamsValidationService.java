package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.teams.ProjectTeamsDto;
import com.tracker.job_ts.project.exception.projectTeam.ProjectTeamsValidationException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProjectTeamsValidationService {
    public Mono<Void> validate(ProjectTeamsDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            return Mono.error(new ProjectTeamsValidationException("Name is required"));
        }
        return Mono.empty();
    }
}