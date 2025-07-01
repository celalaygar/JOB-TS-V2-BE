package com.tracker.job_ts.projectTask.model;

public enum ProjectTaskSystemStatus {
    ACTIVE(1, "ACTIVE", "AKTİF"),
    PASSIVE(2, "PASSIVE", "PASİF"),
    DELETED(3, "DELETED", "SİLİNMİLŞ") ;

    private final int id;
    private final String en;
    private final String tr;

    ProjectTaskSystemStatus(int id, String en, String tr) {
        this.id = id;
        this.en = en;
        this.tr = tr;
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

    public static ProjectTaskSystemStatus fromId(int id) {
        for (ProjectTaskSystemStatus sprintResponseStatus : values()) {
            if (sprintResponseStatus.getId() == id) {
                return sprintResponseStatus;
            }
        }
        throw new IllegalArgumentException("Geçersiz Project Task System Status ID: " + id);
    }
}