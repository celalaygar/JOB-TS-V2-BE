package com.tracker.job_ts.projectTask.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.projectTask.dto.taskComment.TaskCommentRequestDto;
import com.tracker.job_ts.projectTask.dto.taskComment.TaskCommentResponseDto;
import com.tracker.job_ts.projectTask.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(ApiPaths.TaskCommentCtrl.CTRL)
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    // Yorum Ekleme
    @PostMapping("/add")
    public Mono<TaskCommentResponseDto> addComment(@Valid @RequestBody TaskCommentRequestDto dto) {
        return taskCommentService.addComment(dto);
    }

    // Yorumları Listeleme
    @GetMapping("/{taskId}")
    public Flux<TaskCommentResponseDto> getCommentsByTaskId(@PathVariable String taskId) {
        return taskCommentService.getCommentsByTaskId(taskId);
    }

    // Yorumu Güncelleme
    @PutMapping("/update/{commentId}/task/{taskId}")
    public Mono<ResponseEntity<TaskCommentResponseDto>> updateComment(
            @PathVariable String commentId,
            @PathVariable String taskId,
            @Valid @RequestBody TaskCommentRequestDto dto) {
        return taskCommentService.updateComment(commentId, taskId, dto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Yorumu Silme
    @DeleteMapping("/delete/{commentId}/task/{taskId}")
    public Mono<Boolean> deleteComment(
            @PathVariable String commentId,
            @PathVariable String taskId) {
        return taskCommentService.deleteComment(commentId, taskId);
    }
}