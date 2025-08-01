package com.tracker.job_ts.project.model;


import com.tracker.job_ts.project.entity.ProjectUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRoleModel {
    private String id;
    private String name;
    private String description;

    public ProjectRoleModel(ProjectUserRole role){
        this.id = role.getId();
        this.name = role.getRoleName();
        this.description = role.getDescription();
    }
}
