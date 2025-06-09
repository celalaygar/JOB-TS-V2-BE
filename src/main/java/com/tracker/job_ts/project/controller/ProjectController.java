package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.ProjectRequestDto;
import com.tracker.job_ts.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.tracker.job_ts.project.dto.ProjectDto;

@RestController
@RequestMapping(ApiPaths.ProjectsCtrl.CTRL)
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public Mono<ResponseEntity<ProjectDto>> create(@RequestBody ProjectRequestDto dto) {
        return projectService.create(dto)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProjectDto>> getById(@PathVariable String id) {
        return projectService.getById(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProjectDto>> update(@PathVariable String id, @RequestBody ProjectDto dto) {
        return projectService.update(id, dto)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Flux<ProjectDto> getAll() {

        return projectService.getAll();
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return projectService.delete(id)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
