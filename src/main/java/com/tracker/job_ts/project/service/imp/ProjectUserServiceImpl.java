package com.tracker.job_ts.project.service.imp;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import com.tracker.job_ts.project.mapper.ProjectUserMapper;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.project.service.ProjectUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class ProjectUserServiceImpl implements ProjectUserService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final AuthHelperService authHelperService;


    @Override
    public Flux<ProjectUserResponseDto> listProjectUsers(String projectId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        projectUserRepository.findByProjectIdAndUserId(projectId, authUser.getId())
                                .switchIfEmpty(Mono.error(new AccessDeniedException("No access to this project.")))
                )
                .thenMany(projectUserRepository.findByProjectId(projectId))
                .flatMap(projectUser ->
                        userRepository.findById(projectUser.getUserId())
                                .map(user -> {
                                    user.setProjectSystemRole(projectUser.getProjectSystemRole());
                                    return ProjectUserMapper.toDto(user);
                                })
                );
    }



    @Override
    public Mono<Void> addUserToProject(ProjectUserDTO dto) {
        return userRepository.findByEmail(dto.getEmail())
                .switchIfEmpty(Mono.error(new NoSuchElementException("User not found.")))
                .flatMap(user ->
                        projectRepository.findById(dto.getProjectId())
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                .flatMap(project -> {
                                    if (project.getUsers() == null) {
                                        project.setUsers(new ArrayList<>());
                                    }
                                    project.getUsers().add(user);
                                    return projectRepository.save(project).then();
                                })
                );
    }

    @Override
    public Mono<Void> removeUserFromProject(ProjectUserDTO dto) {
        return userRepository.findByEmail(dto.getEmail())
                .switchIfEmpty(Mono.error(new NoSuchElementException("User not found.")))
                .flatMap(user ->
                        projectRepository.findById(dto.getProjectId())
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                .flatMap(project -> {
                                    if (project.getUsers() != null) {
                                        project.getUsers().removeIf(u -> u.getEmail().equals(user.getEmail()));
                                    }
                                    return projectRepository.save(project).then();
                                })
                );
    }
}