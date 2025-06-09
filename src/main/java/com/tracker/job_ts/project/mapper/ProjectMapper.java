package com.tracker.job_ts.project.mapper;

import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectRequestDto;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ProjectMapper {

    public ProjectDto toDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .progress(project.getProgress())
                .projectCode(project.getProjectCode())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .priority(project.getPriority())
                .tags(project.getTags())
                .repository(project.getRepository())
                .build();
    }

    public ProjectDto toDto(Project project, ProjectUser projectUser) {
        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .status(project.getStatus())
                .progress(project.getProgress())
                .projectCode(project.getProjectCode())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .priority(project.getPriority())
                .tags(project.getTags())
                .repository(project.getRepository())
                .me(new ProjectUserDTO(projectUser))
                .build();
    }
    public Project projectRequestToEntity(ProjectRequestDto dto) {
        return Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .progress(dto.getProgress())
                .createdDate(new Date())
                .projectSystemStatus(ProjectSystemStatus.ACTIVE)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .priority(dto.getPriority())
                .tags(dto.getTags())
                .repository(dto.getRepository())
                .build();
    }
    public Project toEntity(ProjectDto dto) {
        return Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .progress(dto.getProgress())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .priority(dto.getPriority())
                .tags(dto.getTags())
                .repository(dto.getRepository())
                .build();
    }
}
