package com.tracker.job_ts.project.dto;
import com.tracker.job_ts.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    public ProjectUserDTO(User currentUser) {
        this.userId = currentUser.getId();
        this.firstname = currentUser.getFirstname();
        this.lastname = currentUser.getLastname();
        this.email = currentUser.getEmail();
    }
}
