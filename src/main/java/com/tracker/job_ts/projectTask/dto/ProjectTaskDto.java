package com.tracker.job_ts.projectTask.dto;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.model.AssaignSprint;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import com.tracker.job_ts.projectTask.entity.ProjectTaskComment;
import com.tracker.job_ts.projectTask.model.*;
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
    private ProjectTaskStatusModel projectTaskStatus;
    private CreatedProject createdProject;
    private ParentTask parentTask;

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
        this.projectTaskStatus = entity.getProjectTaskStatus();
        this.createdProject = entity.getCreatedProject();
        this.parentTask=entity.getParentTask();
    }

    public ProjectTaskDto(ProjectTask entity, Project project) {
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
        this.projectTaskStatus = entity.getProjectTaskStatus();
        this.createdProject = new CreatedProject(project);
        this.parentTask=entity.getParentTask();
    }
}
