package com.tracker.job_ts.projectTask.model;

public enum ProjectTaskType {
    BUG(1, "BUG", "BUG"),
    FEATURE(2, "FEATURE", "ÖZELLİK"),
    SUBTASK(3, "SUBTASK", "ALT TASK"),
    STORY(4, "STORY", "HİKAYE") ;

    private final int id;
    private final String en;
    private final String tr;

    ProjectTaskType(int id, String enDescription, String trDescription) {
        this.id = id;
        this.en = enDescription;
        this.tr = trDescription;
    }

    public int getId() {
        return id;
    }

    public String getEn() {
        return en;
    }
    public String getTr() {
        return tr;
    }

    public static ProjectTaskType fromId(int id) {
        for (ProjectTaskType sprintResponseStatus : values()) {
            if (sprintResponseStatus.getId() == id) {
                return sprintResponseStatus;
            }
        }
        throw new IllegalArgumentException("Geçersiz Project Task Type ID: " + id);
    }
}