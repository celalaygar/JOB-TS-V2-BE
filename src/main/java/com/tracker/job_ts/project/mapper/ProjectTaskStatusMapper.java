package com.tracker.job_ts.project.mapper;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusDTO;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusResponseDTO;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import org.springframework.stereotype.Component;

@Component
public class ProjectTaskStatusMapper {

    public ProjectTaskStatusResponseDTO toDto(ProjectTaskStatus entity) {
        return ProjectTaskStatusResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .label(entity.getLabel())
                .english(entity.getEnglish())
                .turkish(entity.getTurkish())
                .order(entity.getOrder())
                .color(entity.getColor())
                .build();
    }

    public ProjectTaskStatus toEntity(ProjectTaskStatusDTO dto, Project project) {
        return ProjectTaskStatus.builder()
                .id(dto.getId())
                .createdProject(new CreatedProject(project))
                .name(dto.getName())
                .label(dto.getLabel())
                .english(dto.getEnglish())
                .turkish(dto.getTurkish())
                .order(dto.getOrder())
                .color(dto.getColor())
                .build();
    }
    public ProjectTaskStatus updateToEntity(ProjectTaskStatusDTO dto, Project project) {
        return ProjectTaskStatus.builder()
                .id(dto.getId())
                .createdProject(new CreatedProject(project))
                .name(dto.getName())
                .label(dto.getLabel())
                .english(dto.getEnglish())
                .turkish(dto.getTurkish())
                .color(dto.getColor())
                .build();
    }
    public ProjectTaskStatus toEntity(ProjectTaskStatusDTO dto, Project project, User createdBy) {
        return ProjectTaskStatus.builder()
                .id(dto.getId())
                .createdProject(new CreatedProject(project))
                .name(dto.getName())
                .label(dto.getLabel())
                .english(dto.getEnglish())
                .turkish(dto.getTurkish())
                .order(dto.getOrder())
                .color(dto.getColor())
                .createdBy(new CreatedBy(createdBy))
                .build();
    }
}