package com.tracker.job_ts.project.model;

import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.ProjectUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatedBy {
    @Id
    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String username;

    public CreatedBy(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.username = user.getUsername();
    }
    public CreatedBy(ProjectUser user) {
        this.id = user.getUserId();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.username = user.getUsername();
    }
}
