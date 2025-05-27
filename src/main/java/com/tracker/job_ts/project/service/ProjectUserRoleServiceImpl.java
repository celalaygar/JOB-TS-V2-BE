package com.tracker.job_ts.project.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectUserRole.ProjectUserRoleDto;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectUserRole;
import com.tracker.job_ts.project.mapper.PermissionMapperService;
import com.tracker.job_ts.project.mapper.ProjectUserRoleMapper;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectRoleRepository;
import com.tracker.job_ts.project.validator.ProjectRoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProjectUserRoleServiceImpl implements ProjectUserRoleService {

    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRoleValidator validator;
    private final AuthHelperService authHelperService;
    private final ProjectRepository projectRepository;
    private final PermissionMapperService permissionMapperService;

    @Override
    public Mono<ProjectUserRoleDto> create(ProjectUserRoleDto dto) {
        validator.validate(dto);

        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        projectRepository.findByIdAndCreatedByUserId(dto.getProjectId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Project not found")))
                                .flatMap(project ->
                                        permissionMapperService.mapPermissionsToValues(dto.getPermissions())
                                                .flatMap(permissionValues -> {
                                                    ProjectUserRole entity = ProjectUserRoleMapper.toEntity(dto, authUser, project);
                                                    entity.setCreatedAt(LocalDateTime.now());
                                                    entity.setUpdatedAt(LocalDateTime.now());
                                                    entity.setPermissionDetails(permissionValues);
                                                    return projectRoleRepository.save(entity).map(ProjectUserRoleMapper::toDto);
                                                })
                                )
                );
    }

    @Override
    public Mono<ProjectUserRoleDto> update(String id, ProjectUserRoleDto dto) {
        validator.validate(dto);

        return getAuthenticatedProject(dto.getProjectId())
                .flatMap(project ->
                        projectRoleRepository.findByIdAndCreatedProjectId(id, project.getId())
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Role not found")))
                                .flatMap(existing ->
                                        permissionMapperService.mapPermissionsToValues(dto.getPermissions())
                                                .flatMap(permissionValues -> {
                                                    existing.setName(dto.getName());
                                                    existing.setDescription(dto.getDescription());
                                                    existing.setIsDefaultRole(dto.getIsDefaultRole());
                                                    existing.setUpdatedAt(LocalDateTime.now());
                                                    existing.setPermissions(dto.getPermissions());
                                                    existing.setPermissionDetails(permissionValues);
                                                    return projectRoleRepository.save(existing)
                                                            .map(ProjectUserRoleMapper::toDto);
                                                })
                                )
                );
    }
    @Override
    public Mono<ProjectUserRoleDto> getById(String id) {
        return projectRoleRepository.findById(id)
                .map(ProjectUserRoleMapper::toDto)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Role not found")));
    }

    @Override
    public Flux<ProjectUserRoleDto> getAllByProjectId(String projectId) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser ->
                        projectRoleRepository.findByCreatedByIdAndCreatedProjectId(authUser.getId(), projectId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Role not found")))
                                .map(projectUserRole -> {
                                            ProjectUserRoleDto dto = ProjectUserRoleMapper.toDto(projectUserRole);
                                            dto.setPermissionDetails(projectUserRole.getPermissionDetails());
                                            return dto;
                                        }
                                )
                );
    }


    @Override
    public Mono<Void> delete(String id) {
        return projectRoleRepository.deleteById(id);
    }
    // ✅ Yardımcı metot: Authenticated user ile proje doğrulama
    private Mono<Project> getAuthenticatedProject(String projectId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        projectRepository.findByIdAndCreatedByUserId(projectId, authUser.getId())
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found")))
                );
    }
}
