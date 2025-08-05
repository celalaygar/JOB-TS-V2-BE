package com.tracker.job_ts.projectTask.model;

public enum ProjectTaskPriority {
    CRITICAL(1, "CRITICAL", "KIRİTİK"),
    HIGH(2, "PLANNED", "YÜKSEK"),
    MEDIUM(3, "MEDIUM", "ORTA"),
    LOW(4, "LOW", "DÜŞÜK") ;

    private final int id;
    private final String en;
    private final String tr;

    ProjectTaskPriority(int id, String en, String tr) {
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

    public static ProjectTaskPriority fromId(int id) {
        for (ProjectTaskPriority sprintResponseStatus : values()) {
            if (sprintResponseStatus.getId() == id) {
                return sprintResponseStatus;
            }
        }
        throw new IllegalArgumentException("Geçersiz Project Task Priority ID: " + id);
    }
}