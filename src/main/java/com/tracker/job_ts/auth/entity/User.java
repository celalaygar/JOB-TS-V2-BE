package com.tracker.job_ts.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Document("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private Set<SystemRole> systemRoles;
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
}
