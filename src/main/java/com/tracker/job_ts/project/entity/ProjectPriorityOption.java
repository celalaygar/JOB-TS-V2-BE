package com.tracker.job_ts.project.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "project_priority_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPriorityOption {
    @Id
    private String id;
    private String projectId;
    private String value;
    private String label;
}