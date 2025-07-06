package com.tracker.job_ts.sprint.dto;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.sprint.entity.SprintUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

// SprintUser Detay DTO'su (Yanıt için)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintUserDto {
    private String id;
    private String sprintId;
    private String projectId;
    private CreatedBy user;
    private CreatedProject createdProject;
    private Instant assignmentDate;
    private String roleInSprint;
    private String statusInSprint;
    private Integer estimatedEffort;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SprintUserDto(SprintUser sprintUser) {
        this.id = sprintUser.getId();
        this.sprintId = sprintUser.getSprintId();
        this.projectId = sprintUser.getProjectId();
        this.user = sprintUser.getUser();
        this.createdProject = sprintUser.getCreatedProject();
        this.assignmentDate = sprintUser.getAssignmentDate();
        this.roleInSprint = sprintUser.getRoleInSprint();
        this.statusInSprint = sprintUser.getStatusInSprint();
        this.estimatedEffort = sprintUser.getEstimatedEffort();
        this.notes = sprintUser.getNotes();
        this.createdAt = sprintUser.getCreatedAt();
        this.updatedAt = sprintUser.getUpdatedAt();
    }
}
