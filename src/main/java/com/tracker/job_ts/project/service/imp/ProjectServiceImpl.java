package com.tracker.job_ts.project.service.imp;

import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectRequestDto;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.mapper.ProjectMapper;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.project.service.ProjectService;
import com.tracker.job_ts.project.service.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
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
                    CreatedBy createdBy = new CreatedBy(currentUser);
                    Project entity = mapper.projectRequestToEntity(dto);
                    entity.setCreatedBy(createdBy);
                    entity.setProjectCode(generateProjectCode(entity.getName())); // <-- Burada kod set ediliyor
                    entity.setCreatedAt(LocalDateTime.now());
                    entity.setUpdatedAt(LocalDateTime.now());
                    return repository.save(entity)
                            .flatMap(savedProject -> {
                                // ProjectUser dokümanı oluştur
                                ProjectUser projectUser = ProjectUser.builder()
                                        .projectId(savedProject.getId())
                                        .userId(currentUser.getId())
                                        .email(currentUser.getEmail())
                                        .firstname(currentUser.getFirstname())
                                        .lastname(currentUser.getLastname())
                                        .isCreator(true)
                                        .isProjectMember(true)
                                        .projectSystemRole(ProjectSystemRole.PROJECT_ADMIN)
                                        .assignedBy(currentUser.getId())
                                        .assignedAt(LocalDateTime.now())
                                        .createdAt(LocalDateTime.now())
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
                        projectUserRepository.findByProjectIdAndUserId(id, currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project or project does not exist.")))
                                .flatMap(projectUser -> {
                                    // Projeyi güncelleyebilmek için PROJECT_ADMIN yetkisine sahip olunması gerektiğini varsayıyoruz.
                                    if (projectUser.getProjectSystemRole() != ProjectSystemRole.PROJECT_ADMIN) {
                                        return Mono.error(new IllegalAccessException("User does not have permission to update this project."));
                                    }
                                    return repository.findById(id)
                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                            .flatMap(existingProject -> {
                                                // DTO'dan gelen verilerle mevcut projenin alanlarını güncelliyoruz.
                                                existingProject.setName(dto.getName());
                                                existingProject.setDescription(dto.getDescription());
                                                existingProject.setStatus(dto.getStatus());
                                                existingProject.setPriority(dto.getPriority());
                                                existingProject.setRepository(dto.getRepository());
                                                existingProject.setStartDate(dto.getStartDate());
                                                existingProject.setEndDate(dto.getEndDate());
                                                existingProject.setTags(dto.getTags());
                                                // create metodundaki gibi proje kodu yeniden oluşturulmaz, mevcut hali korunur.
                                                // createdBy alanı da aynı şekilde korunur.
                                                existingProject.setUpdatedAt(LocalDateTime.now());

                                                return repository.save(existingProject);
                                            })
                                            .map(mapper::toDto);
                                })
                );
    }


    @Override
    public Flux<ProjectDto> getAll() {
        return authHelperService.getAuthUser()
                .flatMapMany(currentUser ->
                        projectUserRepository.findAllByUserIdAndProjectSystemRoleNot(currentUser.getId(),ProjectSystemRole.PROJECT_REMOVED_USER)
                                .map(ProjectUser::getProjectId)
                                .distinct()
                                .collectList()
                                .flatMapMany(projectIds ->
                                        repository.findAllById(projectIds)
                                                .filter(project -> project.getProjectSystemStatus() == ProjectSystemStatus.ACTIVE)
                                                .map(mapper::toDto)
                                )
                );
    }
    @Override
    public Mono<ProjectDto> getById(String id) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser ->
                        projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(id, currentUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new AccessDeniedException("No access to this project.")))
                                .flatMap(projectUser ->
                                        repository.findById(id)
                                                .filter(project -> project.getProjectSystemStatus() == ProjectSystemStatus.ACTIVE)
                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found or inactive.")))
                                                .map(project -> mapper.toDto(project, projectUser))
                                )
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

