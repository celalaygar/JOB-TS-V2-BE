package com.tracker.job_ts.kanban.dto;

import com.tracker.job_ts.projectTask.model.ProjectTaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KanbanTaskFilterRequestDto {

    private Optional<String> projectId;
    private Optional<ProjectTaskType> taskType;
    private Optional<String> assigneeId;
    private Optional<String> sprintId;
    private String searchText; // Yeni alan

}