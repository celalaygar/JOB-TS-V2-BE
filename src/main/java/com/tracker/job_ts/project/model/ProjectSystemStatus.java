package com.tracker.job_ts.project.model;


public enum ProjectSystemStatus {

    ACTIVE("1","ACTIVE"),
    PASSIVE("2","PASSIVE"),
    DELETED("3","DELETED");

    private final String systemStatusId;
    private final String displayName;

    ProjectSystemStatus(String systemStatusId, String displayName) {
        this.systemStatusId = systemStatusId;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
