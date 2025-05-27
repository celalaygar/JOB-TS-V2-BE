package com.tracker.job_ts.projectTask.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "task_type_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskTypeOption {
    @Id
    private String id;
    private String ProjectTaskId;
    private String value;
    private String label;
    private String colorClass;
    private String icon;
}
