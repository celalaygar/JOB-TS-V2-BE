package com.tracker.job_ts.project.dto;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.model.ProjectUserRoleInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUserDTO {
    private String projectId;
    private String userId;
    private String firstname;
    private String lastname;
    private String email;


    private String id;
    private String username;
    private boolean isCreator;
    private boolean isTeamMember;
    private ProjectSystemRole projectSystemRole;
    private List<ProjectUserRoleInfo> projectRoles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public ProjectUserDTO(User currentUser) {
        this.userId = currentUser.getId();
        this.firstname = currentUser.getFirstname();
        this.lastname = currentUser.getLastname();
        this.email = currentUser.getEmail();
    }
    public ProjectUserDTO(ProjectUser currentUser) {
        this.id = currentUser.getId();
        this.username = currentUser.getUsername();
        this.userId = currentUser.getId();
        this.firstname = currentUser.getFirstname();
        this.lastname = currentUser.getLastname();
        this.email = currentUser.getEmail();
        this.isCreator = currentUser.isCreator();
        this.isTeamMember = currentUser.isTeamMember();
        this.projectSystemRole = currentUser.getProjectSystemRole();
        this.projectRoles = currentUser.getProjectRoles();
        this.updatedAt = currentUser.getUpdatedAt();
        this.createdAt = currentUser.getCreatedAt();
    }
}
