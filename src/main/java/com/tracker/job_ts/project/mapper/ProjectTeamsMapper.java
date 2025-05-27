package com.tracker.job_ts.project.mapper;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.teams.ProjectTeamResponseDTO;
import com.tracker.job_ts.project.dto.teams.ProjectTeamsDto;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTeams;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import org.springframework.stereotype.Component;

@Component
public class ProjectTeamsMapper {

    public ProjectTeams toEntity(ProjectTeamsDto dto, Project project, User user) {
        return ProjectTeams.builder()
                .createdProject(new CreatedProject(project))
                .name(dto.getName())
                .description(dto.getDescription())
                .createdBy(new CreatedBy(user))
                .build();
    }

    public ProjectTeams toUpdatedEntity(ProjectTeams existing, ProjectTeamsDto dto, Project project) {
        existing.setCreatedProject(new CreatedProject(project));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        return existing;
    }

    public ProjectTeamResponseDTO toDto(ProjectTeams entity) {
        return ProjectTeamResponseDTO.builder()
                .id(entity.getId())
                .createdProject(entity.getCreatedProject())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdBy(entity.getCreatedBy())
                .build();
    }
}
