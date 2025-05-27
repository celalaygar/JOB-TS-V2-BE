package com.tracker.job_ts.project.dto.teams;

import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTeamsInviteUserRequestDto {
    private String teamId;
    private String email;

}
