package com.tracker.job_ts.projectTask.dto;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.projectTask.model.ProjectTaskPriority;
import com.tracker.job_ts.projectTask.model.ProjectTaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskRequestDto {
    private String id;
    private String title;
    private String description;
    private ProjectTaskPriority priority;
    private ProjectTaskType taskType;
    private ProjectTaskStatusDto projectTaskStatus;
    private String projectTaskStatusId;
    private String projectId;
    private CreatedBy assignee;
    private String assigneeId;
    private String sprintId;
    private String parentTaskId;
}
