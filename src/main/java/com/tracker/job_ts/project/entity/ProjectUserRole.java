package com.tracker.job_ts.project.entity;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.ProjectUserRole.ProjectUserRoleDto;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.model.PermissionValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "project_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUserRole {
    @Id
    private String id;
    private CreatedProject createdProject;
    private String name;
    private String roleName;
    private String description;
    private Integer order;
    private Boolean isDefaultRole;
    private List<String> permissions = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CreatedBy createdBy;
    private List<PermissionValue> permissionDetails; // yeni alan

    public ProjectUserRole(ProjectUserRoleDto dto, Project project, User user) {
        this.id = dto.getId();
        this.createdProject =new CreatedProject(project);
        this.name = dto.getName();
        this.roleName = dto.getName();
        this.description = dto.getName();
        this.isDefaultRole = dto.getIsDefaultRole();
        this.permissions = dto.getPermissions();
        this.createdBy = new CreatedBy(user);
    }
}
