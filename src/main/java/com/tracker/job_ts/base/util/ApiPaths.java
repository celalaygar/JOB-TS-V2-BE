package com.tracker.job_ts.base.util;

public class ApiPaths {
    public static final String BASE_PATH_V2 = "/api/v2";
    private static final String PROJECTS_PATH = "/projects";
    private static final String SPRINT_PATH = "/sprint";

    public static final class ProjectsCtrl {
        public static final String CTRL = BASE_PATH_V2 + PROJECTS_PATH;
    }

    public static final class SprintCtrl {
        public static final String CTRL = BASE_PATH_V2 + SPRINT_PATH;
    }

}
