package com.tracker.job_ts.projectTask.model;

import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedTask {
    private String id;
    private String taskNumber;
    private String title;

    public CreatedTask(ProjectTask task) {
        this.id = task.getId();
        this.taskNumber = task.getTaskNumber();
        this.title = task.getTitle();
    }
}
