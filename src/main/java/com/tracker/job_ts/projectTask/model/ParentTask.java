package com.tracker.job_ts.projectTask.model;

import com.tracker.job_ts.project.model.AssaignSprint;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
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
public class ParentTask {
    private String id;
    private String taskNumber;
    private String title;

    public ParentTask(ProjectTask task){
        this.id=task.getId();
        this.taskNumber=task.getTaskNumber();
        this.title=task.getTitle();
    }
}
