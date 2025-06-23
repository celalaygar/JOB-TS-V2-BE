package com.tracker.job_ts.sprint.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "sprint_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintUser {
    @Id
    private String id;
    private String sprintId;
    private String projectId;
    private String userId;
    private String email;
    private String firstname;
    private String lastname;
    private String username;
    private LocalDateTime addedAt;
}
