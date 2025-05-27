package com.tracker.job_ts.project.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PermissionValue {
    private ProjectRolePermissionEnum key;     // enum name
    private String id;      // permission id
    private boolean value;  // true if in user's permission list
}
