package com.tracker.job_ts.projectTask.dto.projectTask;

import com.tracker.job_ts.projectTask.model.ProjectTaskStatusModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskStatusDto {
    private String id;
    private String name;

    public ProjectTaskStatusDto (ProjectTaskStatusModel status){

        this.id = status.getId();
        this.name = status.getName();
    }
}
