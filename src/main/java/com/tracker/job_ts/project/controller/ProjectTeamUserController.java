package com.tracker.job_ts.project.controller;


import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamUserRequest;
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
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project-team-user")
@RequiredArgsConstructor
public class ProjectTeamUserController {

    private final ProjectTeamUserService projectTeamUserService;

    /**
     * Endpoint to add a user to a specific project team.
     *
     * @param request The ID of the project team.
     * @param request The ID of the user to add.
     * @return A Mono emitting ProjectUserResponseDTO if successful.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProjectUserResponseDto> addUsersToTeam( @Valid @RequestBody ProjectTeamUserRequest request) {
        // Note: projectId from the path is not directly used here as the service layer
        // determines it from the team object. You could pass it if validation or
        // logging at the controller level requires it.
        return projectTeamUserService.addUsersToTeam(request)
                .map(ProjectUserMapper::toDto);
    }

    /**
     * Endpoint to remove a user from a specific project team.
     * Request body must contain projectId, teamId, and userId to remove.
     *
     * @param request A ProjectTeamUserRequest DTO containing project ID, team ID, and user ID to remove.
     * @return A Mono indicating completion.
     */
    @DeleteMapping("/remove") // Differentiate from specific user delete by ID in path
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> removeUserFromTeam(@Valid @RequestBody ProjectTeamUserRequest request) {
        // Path variables projectId and teamId are available, but the method uses values from the request body
        // for consistency with the new approach.
        return projectTeamUserService.removeUserFromTeam(request);
    }

    /**
     * Retrieves all users belonging to a specific project team.
     * Request body must contain projectId and teamId.
     *
     * @param request A ProjectTeamUserRequest DTO containing project ID and team ID.
     * @return A Flux emitting ProjectUserResponseDTO objects belonging to the team.
     */
    @PostMapping("/in-team") // New path for clarity, differentiate from /not-in-team
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProjectUserResponseDto> getUsersByTeamId(@Valid @RequestBody ProjectTeamUserRequest request) {
        return projectTeamUserService.getUsersByTeamId(request)
                .map(ProjectUserMapper::toDto);
    }

    /**
     * Belirli bir projede yer alan ancak belirli bir takımda bulunmayan tüm kullanıcıları getirir.
     * Request body'sinde projectId ve teamId belirtilmelidir.
     *
     * @param request Project ID ve Team ID içeren ProjectTeamUserRequest DTO'su.
     * @return Takımda olmayan ProjectUserResponseDTO nesnelerinin bir Flux'ı.
     */
    @PostMapping("/not-in-team") // Yeni endpoint path'i
    @ResponseStatus(HttpStatus.OK)
    public Flux<ProjectUserResponseDto> getUsersNotInTeam(@Valid @RequestBody ProjectTeamUserRequest request) {
        // Path değişkenlerinden projectId ve teamId'yi almadığımız için DTO'dan direkt kullanıyoruz.
        // Eğer path'ten almak isteseydik, @PathVariable kullanırdık.
        return projectTeamUserService.getUsersNotInTeam(request)
                .map(ProjectUserMapper::toDto);
    }
}