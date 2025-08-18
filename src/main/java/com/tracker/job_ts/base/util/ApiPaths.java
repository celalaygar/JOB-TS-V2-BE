package com.tracker.job_ts.base.util;

public class ApiPaths {
    public static final String BASE_PATH_V2 = "/api/v2";
    private static final String PROJECTS_PATH = "/projects";
    private static final String BACKLOG_PATH = "/backlog";
    private static final String KANBAN_PATH = "/kanban";
    private static final String SPRINT_PATH = "/sprint";
    private static final String SPRINT_TASK_PATH = "/sprint-task";
    private static final String PROJECT_TASK_PATH = "/project-task";
    private static final String SPRINT_USER_PATH = "/sprint-user";
    private static final String TASK_COMMENT_PATH = "/task-comment";

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
