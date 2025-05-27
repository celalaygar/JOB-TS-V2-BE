package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.ProjectUserRole.ProjectUserRoleDto;
import com.tracker.job_ts.project.service.ProjectUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project-user-roles")
@RequiredArgsConstructor
public class ProjectUserRoleController {

    private final ProjectUserRoleService service;

    @PostMapping
    public Mono<ProjectUserRoleDto> create(@RequestBody ProjectUserRoleDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Mono<ProjectUserRoleDto> update(@PathVariable String id, @RequestBody ProjectUserRoleDto dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public Mono<ProjectUserRoleDto> getById(@PathVariable String id) {
        return service.getById(id);
    }

    @GetMapping("/project/{projectId}")
    public Flux<ProjectUserRoleDto> getAllByProjectId(@PathVariable String projectId) {
        return service.getAllByProjectId(projectId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
