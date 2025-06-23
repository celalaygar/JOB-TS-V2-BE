package com.tracker.job_ts.sprint.model;

import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusOnCompletion {
    private String id;
    private String name;
    private String label;
    private String turkish;
    private String english;
    private Integer order;
    private String color;
    private CreatedProject createdProject;
    private CreatedBy createdBy;


    public TaskStatusOnCompletion(ProjectTaskStatus status) {
        this.id = status.getId();
        this.name = status.getName();
        this.label = status.getLabel();
        this.turkish = status.getTurkish();
        this.english = status.getEnglish();
        this.order = status.getOrder();
        this.createdProject = status.getCreatedProject();
        this.createdBy = status.getCreatedBy();
    }
}