package com.tracker.job_ts.project.exception.projectTeam;

public class ProjectTeamValidationException extends RuntimeException {
    public ProjectTeamValidationException(String message) {
        super(message);
    }

    public ProjectTeamValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}