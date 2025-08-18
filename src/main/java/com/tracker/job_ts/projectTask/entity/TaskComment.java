package com.tracker.job_ts.projectTask.entity;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "task_comments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskComment {
    @Id
    private String id;
    private String taskId;
    private CreatedProject createdProject;
    private CreatedBy createdBy;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}