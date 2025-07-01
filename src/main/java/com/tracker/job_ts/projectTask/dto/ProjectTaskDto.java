package com.tracker.job_ts.projectTask.dto;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.model.AssaignSprint;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import com.tracker.job_ts.projectTask.entity.ProjectTaskComment;
import com.tracker.job_ts.projectTask.model.ProjectTaskPriority;
import com.tracker.job_ts.projectTask.model.ProjectTaskSystemStatus;
import com.tracker.job_ts.projectTask.model.ProjectTaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskDto {
    private String id;
    private String taskNumber;
    private String title;
    private String description;
    private ProjectTaskSystemStatus systemStatus;
    private ProjectTaskPriority priority;
    private ProjectTaskType taskType;
    private AssaignSprint sprint;
    private String createdAt;
    private String parentTaskId;
    private CreatedBy createdBy;
    private CreatedBy assignee;
    private CreatedProject createdProject;

    public ProjectTaskDto(ProjectTask entity) {
        this.id = entity.getId();
        this.taskNumber = entity.getTaskNumber();
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        this.systemStatus = entity.getSystemStatus();
        this.priority = entity.getPriority();
        this.taskType = entity.getTaskType();
        this.sprint = entity.getSprint();
        this.createdAt = entity.getCreatedAt();
        this.parentTaskId = entity.getParentTaskId();
        this.createdBy = entity.getCreatedBy();
        this.assignee = entity.getAssignee();
        this.createdProject = entity.getCreatedProject();
    }

}
