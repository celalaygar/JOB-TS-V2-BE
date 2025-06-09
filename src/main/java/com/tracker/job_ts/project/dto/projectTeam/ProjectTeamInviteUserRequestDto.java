package com.tracker.job_ts.project.dto.projectTeam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTeamInviteUserRequestDto {
    private String teamId;
    private String email;

}
