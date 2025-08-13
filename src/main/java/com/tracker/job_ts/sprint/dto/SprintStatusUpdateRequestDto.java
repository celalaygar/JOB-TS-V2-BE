package com.tracker.job_ts.sprint.dto;

import com.tracker.job_ts.sprint.entity.SprintStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SprintStatusUpdateRequestDto {
    private String sprintId;
    private SprintStatus newStatus;
}