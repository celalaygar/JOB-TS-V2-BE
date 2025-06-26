package com.tracker.job_ts.sprint.entity;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.sprint.model.TaskStatusOnCompletion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Document(collection = "sprints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint {
    @Id
    private String id;
    private String name;
    private String description;
    private String sprintCode;
    private Date startDate;
    private Date endDate;
    private String status;
    private int totalIssues;
    private int completedIssues;
    private SprintStatus sprintStatus;
    private TaskStatusOnCompletion taskStatusOnCompletion;
    private CreatedProject createdProject;
    private CreatedBy createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
