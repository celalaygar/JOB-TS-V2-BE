package com.tracker.job_ts.base.util;

public class ApiPaths {
    public static final String BASE_PATH_V2 = "/api/v2";
    public static final String PUBLIC_PATH = BASE_PATH_V2 + "/public";
    public static final String PROJECTS_PATH = "/projects";
    public static final String BACKLOG_PATH = "/backlog";
    public static final String KANBAN_PATH = "/kanban";
    public static final String SPRINT_PATH = "/sprint";
    public static final String SPRINT_TASK_PATH = "/sprint-task";
    public static final String PROJECT_TASK_PATH = "/project-task";
    public static final String SPRINT_USER_PATH = "/sprint-user";
    public static final String TASK_COMMENT_PATH = "/project-task-comment";
    public static final String EMAIL_CHANGE_PATH = "/email-change";
    public static final String WEEKLY_BOARD_PATH = "/weekly-board";
    public static final String EMAIL_CHANGE_PUBLIC_PATH = "/email-change";
    public static final String PASSWORD_RESET_PUBLIC_PATH = "/password-reset";

    public static final class EmailChangePublicCtrl {
        public static final String CTRL = PUBLIC_PATH+ EMAIL_CHANGE_PUBLIC_PATH;
    }
    public static final class PasswordResetPublicCtrl {
        public static final String CTRL = PUBLIC_PATH+ PASSWORD_RESET_PUBLIC_PATH;
    }
    public static final class WeeklyBoardCtrl {
        public static final String CTRL = BASE_PATH_V2 + WEEKLY_BOARD_PATH;
    }

    public static final class EmailChangeCtrl {
        public static final String CTRL = BASE_PATH_V2 + EMAIL_CHANGE_PATH;
    }

    public static final class ProjectsCtrl {
        public static final String CTRL = BASE_PATH_V2 + PROJECTS_PATH;
    }

    public static final class TaskCommentCtrl {
        public static final String CTRL = BASE_PATH_V2 + TASK_COMMENT_PATH;
    }

    public static final class KanbanCtrl {
        public static final String CTRL = BASE_PATH_V2 + KANBAN_PATH;
    }

    public static final class BacklogCtrl {
        public static final String CTRL = BASE_PATH_V2 + BACKLOG_PATH;
    }

    public static final class SprintCtrl {
        public static final String CTRL = BASE_PATH_V2 + SPRINT_PATH;
    }

    public static final class SprintTaskCtrl {
        public static final String CTRL = BASE_PATH_V2 + SPRINT_TASK_PATH;
    }

    public static final class ProjectTaskCtrl {
        public static final String CTRL = BASE_PATH_V2 + PROJECT_TASK_PATH;
    }

    public static final class SprintUserCtrl {
        public static final String CTRL = BASE_PATH_V2 + SPRINT_USER_PATH;
    }
}
