package com.tracker.job_ts.project.entity;


import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.model.ProjectRoleInfo;
import com.tracker.job_ts.project.model.ProjectSystemUserRole;
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
    private String userId;
    private String email;
    private String firstname;
    private String lastname;
    private boolean isCreator;
    private boolean isTeamMember;
    private ProjectSystemUserRole projectSystemUserRole;
    private String assignedBy;
    private LocalDateTime assignedAt;
    private List<ProjectRoleInfo> projectRoles; // Yeni eklenen liste
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

