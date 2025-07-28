package com.tracker.job_ts.project.controller;


import com.tracker.job_ts.project.dto.projectTeam.UserIdsRequest;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import com.tracker.job_ts.project.mapper.ProjectUserMapper;
import com.tracker.job_ts.project.service.ProjectTeamUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/project-teams/{teamId}/users")
@RequiredArgsConstructor
public class ProjectTeamUserController {

    private final ProjectTeamUserService projectTeamUserService;

    /**
     * Endpoint to add a user to a specific project team.
     *
     * @param teamId The ID of the project team.
     * @param userId The ID of the user to add.
     * @return A Mono emitting ProjectUserResponseDTO if successful.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProjectUserResponseDto> addUsersToTeam(@PathVariable String teamId,
                                                       @Valid @RequestBody UserIdsRequest request) {
        // Note: projectId from the path is not directly used here as the service layer
        // determines it from the team object. You could pass it if validation or
        // logging at the controller level requires it.
        return projectTeamUserService.addUsersToTeam(teamId, request.getUserIds())
                .map(ProjectUserMapper::toDto);
    }

    /**
     * Endpoint to remove a user from a specific project team.
     *
     * @param teamId The ID of the project team.
     * @param userId The ID of the user to remove.
     * @return A Mono indicating completion.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeUserFromTeam(@PathVariable String teamId, @PathVariable String userId) {
        return projectTeamUserService.removeUserFromTeam(teamId, userId);
    }

    /**
     * Belirli bir proje takımına ait tüm kullanıcıları getirir.
     *
     * @param projectId İlgili projenin ID'si.
     * @param teamId İlgili proje takımının ID'si.
     * @return Takıma ait ProjectUserResponseDTO nesnelerinin bir Flux'ı.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProjectUserResponseDto> getUsersByTeamId(@PathVariable String projectId, @PathVariable String teamId) {
        return projectTeamUserService.getUsersByTeamId(projectId, teamId)
                .map(ProjectUserMapper::toDto);
    }
}