package com.tracker.job_ts.projectTask.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.dto.ProjectTaskRequestDto;
import com.tracker.job_ts.projectTask.service.ProjectTaskService;
import lombok.RequiredArgsConstructor;
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
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProjectTaskDto> create(@RequestBody ProjectTaskRequestDto dto) {
        return projectTaskService.createTask(dto);
    }

    @PutMapping("/{taskId}")
    public Mono<ProjectTaskDto> update(@PathVariable String taskId,
                                       @RequestBody ProjectTaskRequestDto dto) {
        return projectTaskService.updateTask(taskId, dto);
    }

    @GetMapping("/project/{projectId}")
    public Flux<ProjectTaskDto> getAllByProject(@PathVariable String projectId) {
        return projectTaskService.getAllByProjectId(projectId);
    }
}
