package com.tracker.job_ts.projectTask.mapper;

import com.tracker.job_ts.projectTask.dto.TaskStatusOptionDTO;
import com.tracker.job_ts.projectTask.entity.TaskStatusOption;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusOptionMapper {

    public TaskStatusOptionDTO toDto(TaskStatusOption entity) {
        return TaskStatusOptionDTO.builder()
                .id(entity.getId())
                .projectTaskStatusId(entity.getProjectTaskStatusId())
                .projectTaskId(entity.getProjectTaskId())
                .value(entity.getValue())
                .label(entity.getLabel())
                .build();
    }

    public TaskStatusOption toEntity(TaskStatusOptionDTO dto) {
        return TaskStatusOption.builder()
                .id(dto.getId())
                .projectTaskStatusId(dto.getProjectTaskStatusId())
                .projectTaskId(dto.getProjectTaskId())
                .value(dto.getValue())
                .label(dto.getLabel())
                .build();
    }
}