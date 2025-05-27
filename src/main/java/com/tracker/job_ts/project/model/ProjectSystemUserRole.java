package com.tracker.job_ts.project.model;


public enum ProjectSystemUserRole {

    PROJECT_ADMIN("Project Admin"),
    PROJECT_USER("Project User"),
    PROJECT_OWNER("Project Owner"),
    PROJECT_PASSIVE_USER("Passive User"),
    PROJECT_DELETED_USER("Deleted User");

    private final String displayName;

    ProjectSystemUserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
