package com.tracker.job_ts.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRolePermissionDto {
    private String id;
    private String labelEn;
    private String labelTr;
    private String label;
    private String category;


}
