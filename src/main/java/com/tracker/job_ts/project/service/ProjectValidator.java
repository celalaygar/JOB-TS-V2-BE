package com.tracker.job_ts.project.service;

import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectValidator {

    public void validate(ProjectRequestDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required.");
        }
//        if (dto.getProgress() < 0 || dto.getProgress() > 100) {
//            throw new IllegalArgumentException("Progress must be between 0 and 100.");
//        }
    }

    public void validate(ProjectDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required.");
        }
        if (dto.getProgress() < 0 || dto.getProgress() > 100) {
            throw new IllegalArgumentException("Progress must be between 0 and 100.");
        }
    }
}