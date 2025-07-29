package com.tracker.job_ts.project.entity;


import com.tracker.job_ts.project.model.ProjectUserRoleInfo;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "project_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectUser {
    @Id
    private String id;
    private String projectId;
    private List<String> projectTeamIds; // Bu satırı ekleyin
    private String userId;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private Boolean isCreator;
    private Boolean isProjectMember;
    private Boolean isTeamMember;
    private ProjectSystemRole projectSystemRole;
    private String assignedBy;
    private LocalDateTime assignedAt;
    private List<ProjectUserRoleInfo> projectRoles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

