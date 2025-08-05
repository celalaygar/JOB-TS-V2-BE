package com.tracker.job_ts.backlog.controller;


import com.tracker.job_ts.backlog.dto.BacklogTaskFilterRequestDto;
import com.tracker.job_ts.backlog.service.BacklogService;
import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.BacklogCtrl.CTRL)
@RequiredArgsConstructor
public class BacklogController {

    private final BacklogService backlogService;

    @PostMapping("/filter")
    public Mono<PagedResult<ProjectTaskDto>> getBacklogTasks(
            @Valid @RequestBody BacklogTaskFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return backlogService.getFilteredBacklogTasks(filterDto, page, size);
    }
}