package com.tracker.job_ts.project.dto.projectUser;

import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.model.ProjectUserRoleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserResponseDto {
    private String id;
    private String projectId;
    private String userId;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private Boolean isCreator;
    private Boolean isProjectMember;
    private ProjectSystemRole projectSystemRole;
    private String assignedBy;
    private LocalDateTime assignedAt;
    private List<ProjectUserRoleInfo> projectRoles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
