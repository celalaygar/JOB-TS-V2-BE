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

    public ProjectTaskStatusModel (ProjectTaskStatus status){

        this.id = status.getId();
        this.name = status.getName();
    }
}
