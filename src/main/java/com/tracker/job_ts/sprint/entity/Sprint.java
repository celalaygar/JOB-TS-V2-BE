package com.tracker.job_ts.sprint.entity;

import com.tracker.job_ts.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "sprints")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sprint {
    @Id
    private String id;
    private String projectId;
    private String name;
    private Date startDate;
    private Date endDate;
    private String status;
    private int totalIssues;
    private int completedIssues;
    private List<User> team;
    private List<User> users;
}
