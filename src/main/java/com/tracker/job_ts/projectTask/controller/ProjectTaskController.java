package com.tracker.job_ts.projectTask.controller;

import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.dto.ProjectTaskFltreRequestDto;
import com.tracker.job_ts.projectTask.dto.ProjectTaskRequestDto;
import com.tracker.job_ts.projectTask.service.ProjectTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.ProjectTaskCtrl.CTRL)
@RequiredArgsConstructor
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public Mono<ProjectTaskDto> create(@RequestBody ProjectTaskRequestDto dto) {
        return projectTaskService.createTask( dto);
    }

    @GetMapping("/{taskId}")
    public Mono<ProjectTaskDto> update(@PathVariable String taskId  ) {
        return projectTaskService.getByTaskId(taskId );
    }

    @PutMapping("/{taskId}")
    public Mono<ProjectTaskDto> update(@PathVariable String taskId,
                                       @RequestBody ProjectTaskRequestDto dto) {
        return projectTaskService.updateTask(taskId, dto);
    }

    @PostMapping("/filter")
    public Mono<PagedResult<ProjectTaskDto>> filterTasks(
            @RequestBody ProjectTaskFltreRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return projectTaskService.getAllFilteredTasks(filterDto, page, size);
    }
}
