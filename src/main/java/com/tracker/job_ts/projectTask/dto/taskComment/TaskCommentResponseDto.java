package com.tracker.job_ts.projectTask.dto.taskComment;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.projectTask.entity.TaskComment;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskCommentResponseDto {
    private String id;
    private String taskId;
    private CreatedProject createdProject;
    private CreatedBy createdBy;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TaskCommentResponseDto(TaskComment comment) {
        this.id = comment.getId();
        this.taskId = comment.getTaskId();
        this.createdProject = comment.getCreatedProject();
        this.createdBy = comment.getCreatedBy();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }
}