package com.tracker.job_ts.projectTask.entity;

import com.tracker.job_ts.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskComment {
    private String id;
    private String text;
    private User author;
    private String createdAt;
    private User createdBy;
}