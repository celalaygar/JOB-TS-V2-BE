package com.tracker.job_ts.projectTask.dto;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.projectTask.entity.ProjectTaskComment;
import com.tracker.job_ts.projectTask.model.ProjectTaskPriority;
import com.tracker.job_ts.projectTask.model.ProjectTaskStatusModel;
import com.tracker.job_ts.projectTask.model.ProjectTaskSystemStatus;
import com.tracker.job_ts.projectTask.model.ProjectTaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

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
