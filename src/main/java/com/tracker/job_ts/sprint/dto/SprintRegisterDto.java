package com.tracker.job_ts.sprint.dto;

import com.tracker.job_ts.sprint.entity.SprintStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintRegisterDto {
    @Id
    private String id;
    private String projectId;
    private String projectTaskStatusId;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private String status;
    private int totalIssues;
    private int completedIssues;
    private SprintStatus sprintStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
