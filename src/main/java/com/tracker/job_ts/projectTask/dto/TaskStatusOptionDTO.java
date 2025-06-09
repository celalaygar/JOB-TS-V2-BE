package com.tracker.job_ts.projectTask.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusOptionDTO {
    private String id;
    private String projectTaskStatusId;
    private String projectTaskId;
    private String value;
    private String label;
}