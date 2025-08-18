package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.projectTask.dto.taskComment.TaskCommentRequestDto;
import com.tracker.job_ts.projectTask.dto.taskComment.TaskCommentResponseDto;
import com.tracker.job_ts.projectTask.entity.TaskComment;
import com.tracker.job_ts.projectTask.repository.ProjectTaskRepository;
import com.tracker.job_ts.projectTask.repository.TaskCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectUserRepository projectUserRepository;
    private final AuthHelperService authHelperService;

    // Yorum Ekleme
    public Mono<TaskCommentResponseDto> addComment(TaskCommentRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectTaskRepository.findById(dto.getTaskId())
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Task not found.")))
                        .flatMap(task -> projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                        task.getCreatedProject().getId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                .flatMap(projectUser -> {
                                    TaskComment newComment = TaskComment.builder()
                                            .taskId(dto.getTaskId())
                                            .comment(dto.getComment())
                                            .createdProject(task.getCreatedProject())
                                            .createdBy(new CreatedBy(authUser))
                                            .createdAt(LocalDateTime.now())
                                            .updatedAt(LocalDateTime.now())
                                            .build();
                                    return taskCommentRepository.save(newComment)
                                            .map(TaskCommentResponseDto::new);
                                })
                        )
                );
    }

    // Yorumları Listeleme (taskId'ye göre)
    public Flux<TaskCommentResponseDto> getCommentsByTaskId(String taskId) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> projectTaskRepository.findById(taskId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Task not found.")))
                        .flatMapMany(task -> projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                        task.getCreatedProject().getId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                .flatMapMany(projectUser -> taskCommentRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                                        .map(TaskCommentResponseDto::new)
                                )
                        )
                );
    }

    // Yorumu Güncelleme
    public Mono<TaskCommentResponseDto> updateComment(String commentId, String taskId, TaskCommentRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> taskCommentRepository.findByIdAndTaskId(commentId, taskId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Comment not found for the given task.")))
                        .flatMap(existingComment -> {
                            // Yorumun sahibini doğrulama
                            if (!existingComment.getCreatedBy().getId().equals(authUser.getId())) {
                                return Mono.error(new IllegalAccessException("You are not authorized to update this comment."));
                            }

                            // Yorumun ait olduğu projenin üyeliğini kontrol etme
                            return projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                            existingComment.getCreatedProject().getId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                    .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                    .flatMap(projectUser -> {
                                        existingComment.setComment(dto.getComment());
                                        existingComment.setUpdatedAt(LocalDateTime.now());
                                        return taskCommentRepository.save(existingComment)
                                                .map(TaskCommentResponseDto::new);
                                    });
                        })
                );
    }

    // Yorumu Silme
    public Mono<Boolean> deleteComment(String commentId, String taskId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> taskCommentRepository.findByIdAndTaskId(commentId, taskId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Comment not found for the given task.")))
                        .flatMap(existingComment -> {
                            // Yorumun sahibini doğrulama
                            if (!existingComment.getCreatedBy().getId().equals(authUser.getId())) {
                                return Mono.error(new IllegalAccessException("You are not authorized to delete this comment."));
                            }

                            // Yorumun ait olduğu projenin üyeliğini kontrol etme
                            return projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                            existingComment.getCreatedProject().getId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                    .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                    .flatMap(projectUser -> taskCommentRepository.delete(existingComment).thenReturn(true));
                        })
                );
    }
}