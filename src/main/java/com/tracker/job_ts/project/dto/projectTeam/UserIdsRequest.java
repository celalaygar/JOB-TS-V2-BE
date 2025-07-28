package com.tracker.job_ts.project.dto.projectTeam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import jakarta.validation.constraints.NotEmpty; // For validation

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserIdsRequest {
    @NotEmpty(message = "User IDs cannot be empty")
    private List<String> userIds;
}