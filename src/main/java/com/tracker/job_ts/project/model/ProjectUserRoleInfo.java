package com.tracker.job_ts.project.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUserRoleInfo {
    private String projectRoleId;
    private String projectId;
    private String projectRoleName;
    private int projectRoleOrder;
}