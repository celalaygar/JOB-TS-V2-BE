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

@Document(collection = "project_team")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTeam {
    @Id
    private String id;
    private CreatedProject createdProject;
    private String name;
    private String description;
    private CreatedBy createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
