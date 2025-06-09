package com.tracker.job_ts.project.mapper;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamResponseDTO;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamDto;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTeam;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import org.springframework.stereotype.Component;

@Component
public class ProjectTeamMapper {

    public ProjectTeam toEntity(ProjectTeamDto dto, Project project, User user) {
        return ProjectTeam.builder()
                .createdProject(new CreatedProject(project))
                .name(dto.getName())
                .description(dto.getDescription())
                .createdBy(new CreatedBy(user))
                .build();
    }

    public ProjectTeam toUpdatedEntity(ProjectTeam existing, ProjectTeamDto dto, Project project) {
        existing.setCreatedProject(new CreatedProject(project));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return existing;
    }

    public ProjectTeamResponseDTO toDto(ProjectTeam entity) {
        return ProjectTeamResponseDTO.builder()
                .id(entity.getId())
                .createdProject(entity.getCreatedProject())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
