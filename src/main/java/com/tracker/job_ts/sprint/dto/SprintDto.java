package com.tracker.job_ts.sprint.dto;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.sprint.entity.Sprint;
import com.tracker.job_ts.sprint.entity.SprintStatus;
import com.tracker.job_ts.sprint.model.TaskStatusOnCompletion;
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
public class SprintDto {
    private String id;
    private CreatedProject createdProject;
    private String name;
    private String description;
    private String sprintCode;
    private Date startDate;
    private Date endDate;
    private String status;
    private SprintStatus sprintStatus;
    private TaskStatusOnCompletion taskStatusOnCompletion;
    private CreatedBy createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SprintDto(Sprint sprint){
        this.id = sprint.getId();
        this.name = sprint.getName();
        this.description = sprint.getDescription();
        this.createdProject = sprint.getCreatedProject();
        this.sprintCode = sprint.getSprintCode();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();
        this.sprintStatus = sprint.getSprintStatus();
        this.taskStatusOnCompletion = sprint.getTaskStatusOnCompletion();
        this.createdBy = sprint.getCreatedBy();
    }
}