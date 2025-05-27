package com.tracker.job_ts.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectSprint {
    private String id;
    private String projectId;
    private String name;
    private String status;
    private String startDate;
    private String endDate;
    private int progress;
}