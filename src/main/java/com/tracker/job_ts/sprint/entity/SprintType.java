package com.tracker.job_ts.sprint.entity;


public enum SprintType {

    PROJECT("Sprint PLANNED"),
    TEAM("Sprint ACTIVE");

    private final String displayName;

    SprintType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
