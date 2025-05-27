package com.tracker.job_ts.project.service;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public Flux<User> listProjectUsers(String projectId) {
        return projectRepository.findById(projectId)
                .flatMapMany(project -> Flux.fromIterable(project.getUsers()));
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