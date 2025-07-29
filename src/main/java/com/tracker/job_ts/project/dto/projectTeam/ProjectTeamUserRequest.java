package com.tracker.job_ts.project.dto.projectTeam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTeamUserRequest {

    private String projectId;
    private String teamId;
    private String userId;
    private List<String> projectUserIds;
}