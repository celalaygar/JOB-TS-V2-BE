package com.tracker.job_ts.projectTask.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProjectTaskStatusRequestDto {
    private String projectId;
    private String projectTaskId;
    private String projectTaskStatusId;
}