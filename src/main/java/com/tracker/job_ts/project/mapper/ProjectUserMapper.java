package com.tracker.job_ts.project.mapper;


import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import org.springframework.stereotype.Component;

public class ProjectUserMapper {


    public static ProjectUserResponseDto toDto(User user) {
        if (user == null) return null;

        return ProjectUserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .systemRoles(user.getSystemRoles())
                .name(user.getName())
                .initials(user.getInitials())
                .teamRole(user.getTeamRole())
                .companyRole(user.getCompanyRole())
                .status(user.getStatus())
                .department(user.getDepartment())
                .phone(user.getPhone())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .position(user.getPosition())
                .company(user.getCompany())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
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
                .systemRoles(dto.getSystemRoles())
                .name(dto.getName())
                .initials(dto.getInitials())
                .teamRole(dto.getTeamRole())
                .companyRole(dto.getCompanyRole())
                .status(dto.getStatus())
                .department(dto.getDepartment())
                .phone(dto.getPhone())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .position(dto.getPosition())
                .company(dto.getCompany())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }
}
