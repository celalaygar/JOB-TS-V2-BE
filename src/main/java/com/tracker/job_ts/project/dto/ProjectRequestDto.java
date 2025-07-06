package com.tracker.job_ts.project.dto;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.ProjectTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDto {
    private String name;
    private String description;
    private String status;
    private int progress;
    private Integer issueCount;
    private Integer openIssues;
    private List<User> users;
    private List<User> team;
    private User createdBy;
    private String leadId;
    private String startDate;
    private String endDate;
    private String priority;
    private List<String> tags;
    private String repository;
    private Integer sprintCount;
    private Integer milestoneCount;
    private List<ProjectTeam> projectTeams;
}
