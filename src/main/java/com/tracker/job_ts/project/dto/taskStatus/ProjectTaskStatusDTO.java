package com.tracker.job_ts.project.dto.taskStatus;

import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskStatusDTO {

    private String id;

    @NotBlank(message = "Project ID must not be blank")
    private String projectId;

    @NotBlank(message = "Name must not be blank")
    private String name;

    @NotBlank(message = "Label must not be blank")
    private String label;

    @NotBlank(message = "Turkish label must not be blank")
    private String turkish;

    @NotBlank(message = "English label must not be blank")
    private String english;

    @NotBlank(message = "Color must not be blank")
    private String color;

    @NotBlank(message = "Order must not be blank")
    private Integer order;

    private CreatedProject createdProject;
}
