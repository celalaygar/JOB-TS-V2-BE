package com.tracker.job_ts.projectTask.dto.taskComment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class TaskCommentRequestDto {
    @NotBlank(message = "Comment cannot be blank.")
    private String comment;
    @NotBlank(message = "Task ID cannot be blank.")
    private String taskId;
}