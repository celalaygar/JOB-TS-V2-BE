package com.tracker.job_ts.backlog.entity;


import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "backlogs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backlog {
    @Id
    private String id;
    private String name; // genelde "Backlog"
    private String description;
    private CreatedProject createdProject;
    private CreatedBy createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
