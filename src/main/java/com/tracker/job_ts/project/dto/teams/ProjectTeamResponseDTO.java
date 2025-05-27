package com.tracker.job_ts.project.dto.teams;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTeamResponseDTO {
    private String id;
    private String name;
    private String description;
    private CreatedProject createdProject;
    private CreatedBy createdBy;
}