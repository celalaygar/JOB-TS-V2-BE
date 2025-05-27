package com.tracker.job_ts.project.validator;


import com.tracker.job_ts.project.dto.ProjectUserRole.ProjectUserRoleDto;
import org.springframework.stereotype.Component;

@Component
public class ProjectRoleValidator {

    public void validate(ProjectUserRoleDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Role name is required.");
        }
        if (dto.getProjectId() == null || dto.getProjectId().isBlank()) {
            throw new IllegalArgumentException("Project ID is required.");
        }
    }
}
