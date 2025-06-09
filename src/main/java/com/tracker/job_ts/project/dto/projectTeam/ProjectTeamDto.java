package com.tracker.job_ts.project.dto.projectTeam;

import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTeamDto {
    private String id;
    private CreatedProject createdProject;
    private String name;
    private String description;
    private String projectId;

}
