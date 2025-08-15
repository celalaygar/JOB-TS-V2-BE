package com.tracker.job_ts.sprint.entity;

import com.tracker.job_ts.base.model.BaseResponseCode;

public enum SprintUserSystemRole {
    SPRINT_ADMIN(0, "SPRINT ADMIN"),
    SPRINT_MEMBER(1, "SPRINT MEMBER");

    private final int id;
    private final String description;

    SprintUserSystemRole(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

}