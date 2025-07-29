package com.tracker.job_ts.project.mapper;


import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import com.tracker.job_ts.project.entity.ProjectUser;
import org.springframework.stereotype.Component;

public class ProjectUserMapper {

    public static ProjectUserResponseDto toDto(ProjectUser user) {
        if (user == null) return null;

        return ProjectUserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .projectSystemRole(user.getProjectSystemRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isCreator(user.getIsCreator())
                .isProjectMember(user.getIsProjectMember())
                .build();
    }
    public static ProjectUserResponseDto toDto(User user) {
        if (user == null) return null;

        return ProjectUserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .projectSystemRole(user.getProjectSystemRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isCreator(null)
                .isProjectMember(null)
                .build();
    }

    public static User toEntity(ProjectUserResponseDto dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .username(dto.getUsername())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
