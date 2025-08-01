package com.tracker.job_ts.project.entity;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    private String id;
    private String name;
    private String description;
    private String status;
    private int progress;
    private Integer issueCount;
    private Integer openIssues;
    private String projectCode;
    private List<User> users;
    private CreatedBy createdBy;
    private Date createdDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectSystemStatus projectSystemStatus;
    private String startDate;
    private String endDate;
    private String priority;
    private List<String> tags;
    private String repository;
    private Integer sprintCount;
    private Integer milestoneCount;
    private List<ProjectTeam> projectTeams;
}
