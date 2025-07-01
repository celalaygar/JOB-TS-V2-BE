package com.tracker.job_ts.projectTask.service;


import com.tracker.job_ts.projectTask.dto.ProjectTaskRequestDto;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ProjectTaskMapper {

    public Mono<ProjectTask> toEntity(ProjectTaskRequestDto dto) {
        return Mono.just(
                ProjectTask.builder()
                        .id(dto.getId())
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .priority(dto.getPriority())
                        .taskType(dto.getTaskType())
                        .parentTaskId(dto.getParentTaskId())
                        .build()
        );
    }
}
