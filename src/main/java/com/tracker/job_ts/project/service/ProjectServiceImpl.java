package com.tracker.job_ts.project.service;

import com.tracker.job_ts.auth.dto.UserDto;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectRequestDto;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.mapper.ProjectMapper;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import com.tracker.job_ts.project.model.ProjectSystemUserRole;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectMapper mapper;
    private final ProjectValidator validator;
    private final UserRepository userRepository;
    private final AuthHelperService authHelperService;

    @Override
    public Mono<ProjectDto> create(ProjectRequestDto dto) {
        validator.validate(dto);

        return authHelperService.getAuthUser() // Login olan kullanıcıyı al
                .flatMap(currentUser -> {
                    ProjectUserDTO projectUserDTO = new ProjectUserDTO(currentUser);
                    Project entity = mapper.projectRequestToEntity(dto);
                    entity.setCreatedBy(projectUserDTO);
                    entity.setProjectCode(generateProjectCode(entity.getName())); // <-- Burada kod set ediliyor
                    entity.setCreatedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());
                    return repository.save(entity)
                            .flatMap(savedProject -> {
                                // ProjectUser dokümanı oluştur
                                ProjectUser projectUser = ProjectUser.builder()
                                        .projectId(savedProject.getId())
                                        .userId(currentUser.getId())
                                        .isCreator(true)
                                        .isTeamMember(true)
                                        .projectSystemUserRole(ProjectSystemUserRole.PROJECT_ADMIN)
                                        .assignedBy(currentUser.getId())
                                        .assignedAt(LocalDateTime.now())
                                        .build();

                                projectUser.setCreatedAt(LocalDateTime.now());
                                projectUser.setUpdatedAt(LocalDateTime.now());
                                return projectUserRepository.save(projectUser)
                                        .thenReturn(mapper.toDto(savedProject));
                            });
                });
    }

    @Override
    public Mono<ProjectDto> update(String id, ProjectDto dto) {
        validator.validate(dto);
        return authHelperService.getAuthUser()
                .flatMap(currentUser ->
                        repository.findByIdAndCreatedByUserIdAndProjectSystemStatus(
                                        id, currentUser.getId(), ProjectSystemStatus.ACTIVE)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                .flatMap(project -> {
                                    Project updated = mapper.toEntity(dto);
                                    updated.setId(id);
                                    return repository.save(updated);
                                })
                                .map(mapper::toDto)
                );
    }


    @Override
    public Flux<ProjectDto> getAll() {
        return authHelperService.getAuthUser()
                .flatMapMany(currentUser ->
                        repository.findAllByCreatedByUserIdAndProjectSystemStatus(
                                currentUser.getId(), ProjectSystemStatus.ACTIVE)
                                .map(mapper::toDto)
                );
    }

    @Override
    public Mono<ProjectDto> getById(String id) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser ->
                        repository.findByIdAndCreatedByUserIdAndProjectSystemStatus(
                                        id, currentUser.getId(), ProjectSystemStatus.ACTIVE)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("No have permission to get this project.")))
                                .map(mapper::toDto)
                );
    }
    @Override
    public Mono<Void> delete(String id) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser ->
                        repository.findByIdAndCreatedByUserIdAndProjectSystemStatus(
                                        id, currentUser.getId(), ProjectSystemStatus.ACTIVE)
                                .switchIfEmpty(Mono.error(new AccessDeniedException("No have permission to delete this project.")))
                                .flatMap(project -> {
                                    project.setProjectSystemStatus(ProjectSystemStatus.PASSIVE);
                                    return repository.save(project);
                                })
                                .then()
                );
    }


    private String generateProjectCode(String name) {
        String prefix = name.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
        prefix = prefix.length() > 3 ? prefix.substring(0, 3) : prefix;
        String unique = Long.toHexString(System.currentTimeMillis()).toUpperCase();
        return prefix + "-" + unique.substring(unique.length() - 5);
    }

}

