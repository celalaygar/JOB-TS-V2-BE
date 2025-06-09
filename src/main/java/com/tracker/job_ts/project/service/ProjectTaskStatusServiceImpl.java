package com.tracker.job_ts.project.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusDTO;
import com.tracker.job_ts.project.dto.taskStatus.ProjectTaskStatusResponseDTO;
import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.exception.ProjectTaskStatusNotFoundException;
import com.tracker.job_ts.project.exception.ProjectTaskStatusValidationException;
import com.tracker.job_ts.project.mapper.ProjectTaskStatusMapper;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static com.tracker.job_ts.project.service.ProjectTaskStatusValidationService.validateProjectTaskStatus;
@Service
@RequiredArgsConstructor
public class ProjectTaskStatusServiceImpl implements ProjectTaskStatusService {

    private final ProjectTaskStatusRepository repository;
    private final ProjectRepository projectRepository;
    private final ProjectTaskStatusMapper mapper;
    private final AuthHelperService authHelperService;


    @Override
    public Mono<ProjectTaskStatusResponseDTO> create(ProjectTaskStatusDTO dto) {
        return validateProjectTaskStatus(dto)
                .then(authHelperService.getAuthUser())
                .flatMap(authUser ->
                        projectRepository.findByIdAndCreatedByUserId(dto.getProjectId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found with id: " + dto.getProjectId())))
                                .flatMap(project -> {
                                    return repository.existsByCreatedProjectIdAndName(dto.getProjectId(), dto.getName())
                                            .flatMap(nameExists -> {
                                                if (nameExists) {
                                                    return Mono.error(new ProjectTaskStatusValidationException("This name already exists in the project"));
                                                }
                                                return repository.existsByCreatedProjectIdAndOrder(dto.getProjectId(), dto.getOrder())
                                                        .flatMap(orderExists -> {
                                                            if (orderExists) {
                                                                // En büyük order'ı bul ve +1 yap
                                                                return repository.findByCreatedProjectIdOrderByOrderDesc(dto.getProjectId())
                                                                        .next()
                                                                        .map(maxStatus -> maxStatus.getOrder() + 1)
                                                                        .flatMap(adjustedOrder -> {
                                                                            ProjectTaskStatus entity = mapper.toEntity(dto, project, authUser);
                                                                            entity.setOrder(adjustedOrder);
                                                                            entity.setCreatedAt(LocalDateTime.now());
                                                                            entity.setUpdatedAt(LocalDateTime.now());
                                                                            return repository.save(entity);
                                                                        });
                                                            } else {
                                                                ProjectTaskStatus entity = mapper.toEntity(dto, project, authUser);
                                                                entity.setCreatedAt(LocalDateTime.now());
                                                                entity.setUpdatedAt(LocalDateTime.now());
                                                                return repository.save(entity);
                                                            }
                                                        });
                                            });
                                }))
                .map(mapper::toDto);
    }


    @Override
    public Mono<ProjectTaskStatusResponseDTO> update(String id, ProjectTaskStatusDTO dto) {
        return validateProjectTaskStatus(dto)
                .then(repository.findById(id)
                        .switchIfEmpty(Mono.error(new ProjectTaskStatusNotFoundException("Task status not found with id: " + id)))
                        .flatMap(existing ->
                                projectRepository.findById(dto.getProjectId())
                                        .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found with id: " + dto.getProjectId())))
                                        .flatMap(project -> {
                                            Mono<Void> nameCheck = Mono.empty();
                                            Mono<Integer> resolvedOrder = Mono.just(dto.getOrder());

                                            if (!existing.getName().equals(dto.getName())) {
                                                nameCheck = repository.existsByCreatedProjectIdAndName(dto.getProjectId(), dto.getName())
                                                        .flatMap(nameExists -> {
                                                            if (nameExists) {
                                                                return Mono.error(new ProjectTaskStatusValidationException("This name already exists in the project"));
                                                            }
                                                            return Mono.empty();
                                                        });
                                            }

                                            if (existing.getOrder() != null && !existing.getOrder().equals(dto.getOrder())) {
                                                resolvedOrder = repository.existsByCreatedProjectIdAndOrder(dto.getProjectId(), dto.getOrder())
                                                        .flatMap(orderExists -> {
                                                            if (orderExists) {
                                                                // Order çakışması varsa, en büyük order'ı bulup +1 yap
                                                                return repository.findByCreatedProjectIdOrderByOrderDesc(dto.getProjectId())
                                                                        .next()
                                                                        .map(maxStatus -> maxStatus.getOrder() + 1);
                                                            } else {
                                                                return Mono.just(dto.getOrder());
                                                            }
                                                        });
                                            }

                                            return nameCheck
                                                    .then(resolvedOrder)
                                                    .flatMap(finalOrder -> {
                                                        ProjectTaskStatus updated = mapper.updateToEntity(dto, project);
                                                        updated.setId(id);
                                                        updated.setOrder(finalOrder);
                                                        return repository.save(updated);
                                                    });
                                        })
                        )
                )
                .map(mapper::toDto);
    }



    @Override
    public Mono<ProjectTaskStatusResponseDTO> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ProjectTaskStatusNotFoundException("Task status not found with id: " + id)))
                .map(mapper::toDto);
    }

    @Override
    public Flux<ProjectTaskStatusResponseDTO> getByProjectId(String projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found with id: " + projectId)))
                .flatMapMany(p -> repository.findByCreatedProjectId(projectId))
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ProjectTaskStatusNotFoundException("Task status not found with id: " + id)))
                .flatMap(existing -> repository.deleteById(id));
    }
}

