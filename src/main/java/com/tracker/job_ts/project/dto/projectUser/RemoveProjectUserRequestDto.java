package com.tracker.job_ts.project.dto.projectUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoveProjectUserRequestDto {
    private String projectId;
    private String userId;
}