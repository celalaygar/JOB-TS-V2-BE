package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.service.ProjectService;
import com.tracker.job_ts.project.service.ProjectUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.tracker.job_ts.project.dto.ProjectDto;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project-users")
@RequiredArgsConstructor
public class ProjectUserController {

    private final ProjectUserService projectUserService;

    @GetMapping("/{projectId}")
    public Flux<User> getProjectUsers(@PathVariable String projectId) {
        return projectUserService.listProjectUsers(projectId);
    }

    @PostMapping("/add")
    public Mono<Void> addUser(@RequestBody ProjectUserDTO dto) {
        return projectUserService.addUserToProject(dto);
    }

    @PostMapping("/remove")
    public Mono<Void> removeUser(@RequestBody ProjectUserDTO dto) {
        return projectUserService.removeUserFromProject(dto);
    }
}
