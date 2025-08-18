package com.tracker.job_ts.projectTask.dto.projectTask;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.model.CreatedBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PTProjectDetailDto {
    private String id;
    private String name;
    private String description;
    private String status;
    private int progress;
    private Integer issueCount;
    private Integer openIssues;
    private List<User> users;
    private List<User> team;
    private CreatedBy createdBy;
    private String projectCode;
    private String leadId;
    private String startDate;
    private String endDate;
    private String priority;
    private List<String> tags;
    private String repository;
    private Integer sprintCount;
    private Integer milestoneCount;

    public PTProjectDetailDto(Project project) {
        this.id = project.getId();
        this.name = project.getId();
        this.description = project.getId();
        this.status = project.getId();
        this.projectCode = project.getProjectCode();
        this.tags = project.getTags();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
    }


}
