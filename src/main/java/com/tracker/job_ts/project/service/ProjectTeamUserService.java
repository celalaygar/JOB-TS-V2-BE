package com.tracker.job_ts.project.service;


import com.tracker.job_ts.auth.exception.UserNotFoundException;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamUserRequest;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.exception.projectTeam.ProjectTeamValidationException;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTeamRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectTeamUserService {

    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final AuthHelperService authHelperService;

    /**
     * Adds multiple users to a specific project team.
     * Before adding, it performs several checks for each user:
     * 1. Ensures the authenticated user is the creator of the project.
     * 2. Validates that the project and team exist.
     * 3. Checks if each target user exists.
     * 4. Verifies if each target user is already associated with the project.
     * 5. Prevents adding a user to the team if they are already a member of that specific team.
     *
     * @param request A ProjectTeamUserRequest DTO containing project ID, team ID, and a list of user IDs to add.
     * @return A Flux emitting the updated/created ProjectUser objects for each successfully added user.
     */
    public Flux<ProjectUser> addUsersToTeam(ProjectTeamUserRequest request) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> projectTeamRepository.findByIdAndCreatedProjectId(request.getTeamId(), request.getProjectId())
                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Project Team not found: " + request.getTeamId())))
                        .flatMapMany(projectTeam -> projectRepository.findByIdAndCreatedByUserId(request.getProjectId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found or you are not the creator.")))
                                .flatMapMany(project -> Flux.fromIterable(request.getProjectUserIds())
                                        .flatMap(projectUserId -> projectUserRepository.findByIdAndProjectId(projectUserId, project.getId())
                                                .switchIfEmpty(Mono.error(new UserNotFoundException("Project User not found: " + projectUserId)))
                                                .flatMap(existingProjectUser -> userRepository.findById(existingProjectUser.getUserId())
                                                        .switchIfEmpty(Mono.error(new UserNotFoundException("User to add not found: " + existingProjectUser.getUserId())))
                                                        .flatMap(user ->
                                                        {
                                                            // User is already registered in the project, now add to the team
                                                            List<String> currentTeamIds = Optional.ofNullable(existingProjectUser.getProjectTeamIds())
                                                                    .orElseGet(ArrayList::new);

                                                            if (currentTeamIds.contains(request.getTeamId())) {
                                                                // User is already a member of this team. Throwing an error will stop the whole Flux.
                                                                // If you prefer to skip and continue with other users, return Mono.empty() here.
                                                                return Mono.error(new ProjectTeamValidationException("User " + projectUserId + " is already a member of this team."));
                                                            }

                                                            currentTeamIds.add(request.getTeamId());
                                                            existingProjectUser.setProjectTeamIds(currentTeamIds);
                                                            existingProjectUser.setIsTeamMember(!currentTeamIds.isEmpty());
                                                            existingProjectUser.setUpdatedAt(LocalDateTime.now());
                                                            return projectUserRepository.save(existingProjectUser);
                                                        })
                                                        .switchIfEmpty(
                                                                // User is not registered in the project, throw an error
                                                                Mono.error(new ProjectTeamValidationException("User " + projectUserId + " is not registered in the project. Please register them in the project first."))
                                                        )

                                                )


                                        )
                                )
                        )
                );
    }

    /**
     * Removes a user from a specific project team.
     * Before removing, it performs several checks:
     * 1. Ensures the authenticated user is the creator of the project.
     * 2. Validates that the project and team exist.
     * 3. Checks if the target user exists within the project and is part of the specified team.
     *
     * @param request A ProjectTeamUserRequest DTO containing project ID, team ID, and user ID to remove.
     * @return A Mono indicating completion.
     */
    public Mono<Void> removeUserFromTeam(ProjectTeamUserRequest request) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectTeamRepository.findById(request.getTeamId())
                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Project Team not found: " + request.getTeamId())))
                        .flatMap(projectTeam -> projectRepository.findByIdAndCreatedByUserId(projectTeam.getCreatedProject().getId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found or you are not the creator.")))
                                .flatMap(project -> projectUserRepository.findByProjectIdAndUserId(project.getId(), request.getUserId())
                                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("User is not registered in the project. Cannot remove.")))
                                        .flatMap(existingProjectUser -> {
                                            List<String> currentTeamIds = Optional.ofNullable(existingProjectUser.getProjectTeamIds())
                                                    .orElseGet(ArrayList::new);

                                            if (!currentTeamIds.contains(request.getTeamId())) {
                                                return Mono.error(new ProjectTeamValidationException("User is not a member of this team."));
                                            }

                                            currentTeamIds.remove(request.getTeamId());
                                            existingProjectUser.setProjectTeamIds(currentTeamIds);
                                            existingProjectUser.setIsTeamMember(!currentTeamIds.isEmpty());
                                            existingProjectUser.setUpdatedAt(LocalDateTime.now());
                                            return projectUserRepository.save(existingProjectUser).then();
                                        })
                                )
                        )
                );
    }

    /**
     * Retrieves all users belonging to a specific project team.
     * The authenticated user must be registered in the project.
     *
     * @param request A ProjectTeamUserRequest DTO containing project ID and team ID.
     * @return A Flux emitting ProjectUser objects belonging to the team.
     */
    public Flux<ProjectUser> getUsersByTeamId(ProjectTeamUserRequest request) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> projectUserRepository.findByProjectIdAndUserId(request.getProjectId(), authUser.getId()) // Auth olmuş kullanıcının projede ProjectUser kaydı var mı kontrolü
                        .switchIfEmpty(Mono.error(new ProjectNotFoundException("Login in User not found in Project")))
                        .flatMapMany(authProjectUser -> projectRepository.findById(request.getProjectId()) // Proje var mı kontrolü
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found: " + request.getProjectId())))
                                .flatMapMany(project -> projectTeamRepository.findByIdAndCreatedProjectId(request.getTeamId(), request.getProjectId()) // Takım var mı ve projeye ait mi kontrolü
                                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Proje Takımı bulunamadı veya bu projeye ait değil: " + request.getTeamId())))
                                        // Yeni repository metodunu kullanarak doğrudan sorgulama
                                        .flatMapMany(projectTeam -> projectUserRepository.findByProjectIdAndProjectTeamIdsContaining(request.getProjectId(), request.getTeamId()))
                                )
                        )
                );
    }


    /**
     * Retrieves all users within a specific project who are not part of a given team.
     * The authenticated user must be registered in the project.
     *
     * @param request A ProjectTeamUserRequest DTO containing project ID and team ID.
     * @return A Flux emitting ProjectUser objects that are not in the specified team.
     */
    public Flux<ProjectUser> getUsersNotInTeam(ProjectTeamUserRequest request) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> projectUserRepository.findByProjectIdAndUserId(request.getProjectId(), authUser.getId())
                        .switchIfEmpty(Mono.error(new ProjectNotFoundException("Logged-in user is not registered in this project.")))
                        .flatMapMany(authProjectUser -> projectRepository.findById(request.getProjectId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found: " + request.getProjectId())))
                                .flatMapMany(project -> projectTeamRepository.findByIdAndCreatedProjectId(request.getTeamId(), request.getProjectId())
                                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Project Team not found or does not belong to this project: " + request.getTeamId())))
                                        .flatMapMany(projectTeam -> projectUserRepository.findByProjectIdAndProjectTeamIdsNotContaining(request.getProjectId(), request.getTeamId()))
                                )
                        )
                );
    }
}