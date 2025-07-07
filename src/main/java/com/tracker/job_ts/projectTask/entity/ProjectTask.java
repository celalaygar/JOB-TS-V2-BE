package com.tracker.job_ts.projectTask.entity;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.model.AssaignSprint;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.projectTask.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "project_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTask {
    @Id
    private String id;
    private String taskNumber;
    private String title;
    private String description;
    private ProjectTaskSystemStatus systemStatus;
    private ProjectTaskPriority priority;
    private ProjectTaskType taskType;
    private AssaignSprint sprint;
    private String createdAt;
    private ProjectTaskStatusModel projectTaskStatus;
    //private List<ProjectTaskComment> comments;
    private String parentTaskId;
    private CreatedBy createdBy;
    private CreatedBy assignee;
    private CreatedProject createdProject;
    private ParentTask parentTask;
}
