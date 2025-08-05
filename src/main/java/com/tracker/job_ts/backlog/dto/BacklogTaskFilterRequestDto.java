package com.tracker.job_ts.backlog.dto;

import com.tracker.job_ts.projectTask.model.ProjectTaskType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BacklogTaskFilterRequestDto {

    private Optional<String> projectId;
    private Optional<ProjectTaskType> taskType;
    private Optional<String> assigneeId;
    private String searchText; // Yeni alan

}