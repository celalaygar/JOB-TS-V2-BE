package com.tracker.job_ts.projectTask.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "task_status_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusOption {
    @Id
    private String id;
    private String projectTaskStatusId;
    private String projectTaskId;
    private String value;
    private String label;
}