package com.tracker.job_ts.project.mapper;

import com.tracker.job_ts.project.dto.ProjectUserRole.ProjectUserRoleDto;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectUserRole;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;

public class ProjectUserRoleMapper {

    public static ProjectUserRoleDto toDto(ProjectUserRole entity) {
        return ProjectUserRoleDto.builder()
                .id(entity.getId())
                .createdProject(entity.getCreatedProject())
                .name(entity.getName())
                .roleName(entity.getRoleName())
                .description(entity.getDescription())
                .order(entity.getOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .permissions(entity.getPermissions())
                .isDefaultRole(entity.getIsDefaultRole())
                .createdById(entity.getCreatedBy() != null ? entity.getCreatedBy().getId() : null)
                .build();
    }

    public static ProjectUserRole toEntity(ProjectUserRoleDto dto, User createdBy) {
        return ProjectUserRole.builder()
                .id(dto.getId())
                .name(dto.getName())
                .roleName(dto.getRoleName())
                .description(dto.getDescription())
                .order(dto.getOrder())
                .isDefaultRole(dto.getIsDefaultRole())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .permissions(dto.getPermissions())
                .createdBy(new CreatedBy(createdBy))
                .build();
    }
    public static ProjectUserRole toEntity(ProjectUserRoleDto dto, User createdBy, Project project) {
        return ProjectUserRole.builder()
                .id(dto.getId())
                .createdProject(new CreatedProject(project))
                .name(dto.getName())
                .roleName(dto.getRoleName())
                .description(dto.getDescription())
                .permissions(dto.getPermissions())
                .order(dto.getOrder())
                .isDefaultRole(dto.getIsDefaultRole())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .createdBy(new CreatedBy(createdBy))
                .build();
    }
}
