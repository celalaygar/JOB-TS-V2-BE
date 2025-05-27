package com.tracker.job_ts.project.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectBacklog {
    private String id;
    private String projectId;
    private String name;
    private String status;
    private String startDate;
    private String endDate;
    private int progress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}