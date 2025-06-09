package com.tracker.job_ts.project.dto.projectUser;

import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserResponseDto {
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private Set<SystemRole> systemRoles;
    private ProjectSystemRole projectSystemRole;
    private String name;
    private String initials;
    private String teamRole;
    private String companyRole;
    private String status;
    private String department;
    private String phone;
    private Date dateOfBirth;
    private String gender;
    private String position;
    private String company;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
