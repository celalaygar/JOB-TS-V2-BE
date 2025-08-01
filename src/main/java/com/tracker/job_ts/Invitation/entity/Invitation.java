package com.tracker.job_ts.Invitation.entity;

import com.tracker.job_ts.project.model.ProjectRoleModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "invitations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id
    private String id;
    private InvitationStatus status;
    private ProjectSummary project;
    private UserSummary invitedBy;
    private UserSummary invitedUser;
    private ProjectTeamSummary team;
    private LocalDateTime createdAt;
    private String token; // JWT token
    private LocalDateTime tokenExpiry;
    private ProjectRoleModel projectRole;
}