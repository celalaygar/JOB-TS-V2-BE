package com.tracker.job_ts.auth.dto;

import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.entity.UserDepartment;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String email;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private Set<SystemRole> systemRoles;
    private String status;
    private UserDepartment department;
    private String phone;
    private Date dateOfBirth;
    private String gender;
    private String position;
    private String company;
    private LocalDateTime createdAt;
    private ProjectSystemRole projectSystemRole;
    private LocalDateTime updatedAt;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.systemRoles = user.getSystemRoles();
        this.department = user.getDepartment();
        this.phone = user.getPhone();
        this.dateOfBirth = user.getDateOfBirth();
        this.gender = user.getGender();
        this.position = user.getPosition();
        this.company = user.getCompany();
        this.projectSystemRole = user.getProjectSystemRole();
    }
}
