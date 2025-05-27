package com.tracker.job_ts.base.model;

public enum BaseResponseCode {
    CHANGED_STATUS(0, "CHANGED STATUS"),
    NOT_FOUND(0, "NOT FOUND"),
    PLANNED(1, "PLANNED"),
    NOT_PLANNED(2, "NOT PLANNED"),
    ACTIVE(2, "ACTIVE"),
    NOT_ACTIVE(3, "NOT ACTIVE"),
    NOT_COMPLETED(4, "NOT COMPLETED"),
    COMPLETED(5, "COMPLETED"),
    OK(5, "OK"),
    CONFLICT(4, "CONFLICT");

    private final int id;
    private final String description;

    BaseResponseCode(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static BaseResponseCode fromId(int id) {
        for (BaseResponseCode sprintResponseStatus : values()) {
            if (sprintResponseStatus.getId() == id) {
                return sprintResponseStatus;
            }
        }
        throw new IllegalArgumentException("Geçersiz SprintStatus ID: " + id);
    }
}