package com.tracker.job_ts.projectTask.model;

import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskStatusModel {
    private String id;
    private String name;
    private String label;
    private String turkish;
    private String english;
    private String color;
    public ProjectTaskStatusModel (ProjectTaskStatus status){
        this.id = status.getId();
        this.name = status.getName();
        this.label = status.getLabel();
        this.turkish = status.getTurkish();
        this.english = status.getEnglish();
        this.color = status.getColor();
    }
}
