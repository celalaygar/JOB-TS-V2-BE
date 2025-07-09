package com.tracker.job_ts.sprint.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintTaskRequestDto {
    @NotBlank(message = "Sprint ID cannot be blank")
    private String sprintId;

    @NotBlank(message = "Project ID cannot be blank")
    private String projectId;

    // Task ekleme/çıkarma işlemleri için gerekli
    private String taskId; // Task ID'si opsiyonel olabilir, ama bu işlemlerde gerekli
}