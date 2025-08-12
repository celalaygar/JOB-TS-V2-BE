package com.tracker.job_ts.project.model;


public enum ProjectSystemRole {

    PROJECT_ADMIN("Project Admin"),
    PROJECT_USER("Project User"),
    PROJECT_OWNER("Project Owner"),
    PROJECT_PASSIVE_USER("Passive User"),
    PROJECT_REMOVED_USER("Removed User");

    private final String displayName;

    ProjectSystemRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
