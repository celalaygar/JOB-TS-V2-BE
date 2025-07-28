package com.tracker.job_ts.project.exception.projectTaskStatus;

import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ProjectTaskStatusExceptionHandler {

    @ExceptionHandler(ProjectTaskStatusValidationException.class)
    public Mono<ErrorResponse> handleValidationException(ProjectTaskStatusValidationException ex) {
        return Mono.just(new ErrorResponse(ErrorCode.VALIDATION_ERROR, ex.getMessage()));
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public Mono<ErrorResponse> handleProjectNotFound(ProjectNotFoundException ex) {
        return Mono.just(new ErrorResponse(ErrorCode.PROJECT_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ProjectTaskStatusNotFoundException.class)
    public Mono<ErrorResponse> handleTaskStatusNotFound(ProjectTaskStatusNotFoundException ex) {
        return Mono.just(new ErrorResponse(ErrorCode.TASK_STATUS_NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ErrorResponse> handleGenericException(Exception ex) {
        return Mono.just(new ErrorResponse(ErrorCode.GENERIC_ERROR, ex.getMessage()));
    }
}
