package com.tracker.job_ts.projectTask.entity;

import com.tracker.job_ts.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tasks")
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
    private Object status; // Could be TaskStatus or TaskStatusOption
    private Object priority; // Could be TaskPriority or TaskPriorityOption
    private Object taskType; // Could be TaskType or TaskTypeOption
    private String project;
    private String projectId;
    private String projectName;
    private User assignee;
    private Object sprint; // Could be SprintType or SprintTypeOption
    private String createdAt;
    private User createdBy;
    private List<ProjectTaskComment> comments;
    private String parentTaskId;
}
