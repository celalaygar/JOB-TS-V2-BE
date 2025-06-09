package com.tracker.job_ts.project.entity;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "project_task_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskStatus {
    @Id
    private String id;
    private CreatedProject createdProject;
    private String name;
    private String label;
    private String turkish;
    private String english;
    private Integer order;
    private String color;
    private CreatedBy createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}