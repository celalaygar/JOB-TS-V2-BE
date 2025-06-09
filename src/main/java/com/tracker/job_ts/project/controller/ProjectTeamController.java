package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamResponseDTO;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamDto;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamInviteUserRequestDto;
import com.tracker.job_ts.project.service.ProjectTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project-team")
@RequiredArgsConstructor
public class ProjectTeamController {

    private final ProjectTeamService service;

    @PostMapping
    public Mono<ProjectTeamResponseDTO> create(@RequestBody ProjectTeamDto dto) {
        return service.create(dto);
    }

    @PutMapping
    public Mono<ProjectTeamResponseDTO> update(@RequestBody ProjectTeamDto dto) {
        return service.update(dto);
    }

    @PostMapping("/get-team-detail")
    public Mono<ProjectTeamResponseDTO> getById(@RequestBody ProjectTeamDto dto) {
        return service.getById(dto);
    }

    @GetMapping("/project/{projectId}")
    public Flux<ProjectTeamResponseDTO> getByProjectId(@PathVariable String projectId) {
        return service.getByProjectId(projectId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }

    @PostMapping("/invite")
    public Mono<Invitation> inviteUserToTeam(@RequestBody ProjectTeamInviteUserRequestDto dto) {
        return service.inviteUserToTeam(dto);
    }
}
