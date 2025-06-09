package com.tracker.job_ts.project.exception;

public class ProjectTaskStatusNotFoundException extends RuntimeException {
    public ProjectTaskStatusNotFoundException(String message) {
        super(message);
    }
}