package com.tracker.job_ts.sprint.entity;


public enum SprintStatus {

    PLANNED("Sprint PLANNED"),
    ACTIVE("Sprint ACTIVE"),
    COMPLATED("Sprint COMPLATED");

    private final String displayName;

    SprintStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
