package com.tracker.job_ts.project.dto.taskStatus;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskStatusResponseDTO {

    private String id;

    private String name;

    private String label;

    private String turkish;

    private String english;

    private String color;
    private Integer order;
    private CreatedBy createdBy;

    private CreatedProject createdProject;
}
