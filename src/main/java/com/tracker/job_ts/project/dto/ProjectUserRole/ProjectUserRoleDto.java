package com.tracker.job_ts.project.dto.ProjectUserRole;

import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.model.PermissionValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUserRoleDto {
    private String id;
    private String projectId;
    private CreatedProject createdProject;
    private String name;
    private String roleName;
    private String description;
    private Integer order;
    private Boolean isDefaultRole;
    private List<String> permissions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdById;
    private List<PermissionValue> permissionDetails; // yeni alan
}
