package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusDTO;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusResponseDTO;
import com.tracker.job_ts.project.service.ProjectTaskStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project-task-status")
@RequiredArgsConstructor
public class ProjectTaskStatusController {

    private final ProjectTaskStatusService service;

    @PostMapping
    public Mono<ResponseEntity<ProjectTaskStatusResponseDTO>> create(@Valid @RequestBody ProjectTaskStatusDTO dto) {
        return service.create(dto).map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProjectTaskStatusResponseDTO>> update(@PathVariable String id, @Valid @RequestBody ProjectTaskStatusDTO dto) {
        return service.update(id, dto).map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProjectTaskStatusResponseDTO>> getById(@PathVariable String id) {
        return service.getById(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/project/{projectId}")
    public Flux<ProjectTaskStatusResponseDTO> getByProjectId(@PathVariable String projectId) {
        return service.getByProjectId(projectId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
