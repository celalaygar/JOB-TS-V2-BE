package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusDTO;
import com.tracker.job_ts.project.exception.projectTaskStatus.ProjectTaskStatusValidationException;
import reactor.core.publisher.Mono;

public class ProjectTaskStatusValidationService {
    public static Mono<Void> validateProjectTaskStatus(ProjectTaskStatusDTO dto) {
        if (dto.getProjectId() == null || dto.getProjectId().isBlank()) {
            return Mono.error(new ProjectTaskStatusValidationException("Project ID must not be blank"));
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            return Mono.error(new ProjectTaskStatusValidationException("Name must not be blank"));
        }
        if (dto.getLabel() == null || dto.getLabel().isBlank()) {
            return Mono.error(new ProjectTaskStatusValidationException("Label must not be blank"));
        }
        if (dto.getEnglish() == null || dto.getEnglish().isBlank()) {
            return Mono.error(new ProjectTaskStatusValidationException("English must not be blank"));
        }
        if (dto.getTurkish() == null || dto.getTurkish().isBlank()) {
            return Mono.error(new ProjectTaskStatusValidationException("Turkish must not be blank"));
        }
        if (dto.getColor() == null || dto.getColor().isBlank()) {
            return Mono.error(new ProjectTaskStatusValidationException("Color must not be blank"));
        }
        return Mono.empty();
    }
}
