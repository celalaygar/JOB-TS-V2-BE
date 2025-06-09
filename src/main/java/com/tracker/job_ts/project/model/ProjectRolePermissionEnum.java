package com.tracker.job_ts.project.model;


public enum ProjectRolePermissionEnum {
    MANAGE_MEMBERS("manage_members", "Manage Members", "Üyeleri Yönet", "Team"),
    MANAGE_ROLES("manage_roles", "Manage Roles", "Rolleri Yönet", "Team"),
    VIEW_PROJECT("view_project", "View Project", "Projeyi Görüntüle", "Project"),
    EDIT_PROJECT("edit_project", "Edit Project", "Projeyi Düzenle", "Project"),
    DELETE_PROJECT("delete_project", "Delete Project", "Projeyi Sil", "Project"),
    CREATE_TASK("create_task", "Create Tasks", "Görev Oluştur", "Tasks"),
    EDIT_TASK("edit_task", "Edit Tasks", "Görevi Düzenle", "Tasks"),
    DELETE_TASK("delete_task", "Delete Tasks", "Görevi Sil", "Tasks"),
    ASSIGN_TASK("assign_task", "Assign Tasks", "Görev Ata", "Tasks");

    private final String id;
    private final String labelEn;
    private final String labelTr;
    private final String category;

    ProjectRolePermissionEnum(String id, String labelEn, String labelTr, String category) {
        this.id = id;
        this.labelEn = labelEn;
        this.labelTr = labelTr;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public String getLabelEn() {
        return labelEn;
    }

    public String getLabelTr() {
        return labelTr;
    }

    public String getCategory() {
        return category;
    }
}
