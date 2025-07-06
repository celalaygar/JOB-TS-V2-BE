package com.tracker.job_ts.sprint.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTaskStatusRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.sprint.dto.SprintDto;
import com.tracker.job_ts.sprint.dto.SprintRegisterDto;
import com.tracker.job_ts.sprint.entity.Sprint;
import com.tracker.job_ts.sprint.entity.SprintStatus;
import com.tracker.job_ts.sprint.entity.SprintUser;
import com.tracker.job_ts.sprint.model.TaskStatusOnCompletion;
import com.tracker.job_ts.sprint.repository.SprintRepository;
import com.tracker.job_ts.sprint.repository.SprintUserRepository;
import com.tracker.job_ts.sprint.util.GenerationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SprintService {
    private final SprintRepository sprintRepository;
    private final SprintUserRepository sprintUserRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectTaskStatusRepository taskStatusRepository;
    private final AuthHelperService authHelperService;
    private final SprintValidator validator;

    private final ProjectRepository projectRepository;

    public Mono<Sprint> createSprint(SprintRegisterDto dto) {
        validator.validate(dto);

        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                .flatMap(projectUser ->
                                        taskStatusRepository.findById(dto.getProjectTaskStatusId())
                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Task status not found.")))
                                                .zipWith(projectRepository.findById(dto.getProjectId())
                                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                                )
                                                .flatMap(tuple -> {
                                                    ProjectTaskStatus taskStatus = tuple.getT1();
                                                    Project project = tuple.getT2();

                                                    Sprint sprint = Sprint.builder()
                                                            .name(dto.getName())
                                                            .description(dto.getDescription())
                                                            .sprintCode(GenerationCode.generateProjectCode(dto.getName()))
                                                            .startDate(dto.getStartDate())
                                                            .endDate(dto.getEndDate())
                                                            .status(dto.getStatus())
                                                            .totalIssues(dto.getTotalIssues())
                                                            .completedIssues(dto.getCompletedIssues())
                                                            .sprintStatus(SprintStatus.PLANNED)
                                                            .createdProject(new CreatedProject(project))
                                                            .taskStatusOnCompletion(new TaskStatusOnCompletion(taskStatus))
                                                            .createdBy(new CreatedBy(authUser))
                                                            .createdAt(LocalDateTime.now())
                                                            .updatedAt(LocalDateTime.now())
                                                            .build();

                                                    return sprintRepository.save(sprint)
                                                            .flatMap(savedSprint -> {
                                                                SprintUser sprintUser = SprintUser.builder()
                                                                        .sprintId(savedSprint.getId())
                                                                        .projectId(dto.getProjectId())
                                                                        .user(new CreatedBy(authUser))
                                                                        .createdProject(new CreatedProject(project))
                                                                        .createdAt(LocalDateTime.now())
                                                                        .build();

                                                                return sprintUserRepository.save(sprintUser).thenReturn(savedSprint);
                                                            });
                                                })
                                )
                );
    }

    public Mono<Sprint> updateSprint(String sprintId, SprintRegisterDto dto) {
        validator.validate(dto);
        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        sprintRepository.findById(sprintId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
                                .flatMap(existing -> {
                                    if (!existing.getCreatedBy().getId().equals(authUser.getId())) {
                                        return Mono.error(new IllegalAccessException("Only sprint creator can update it."));
                                    }

                                    return projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), authUser.getId())
                                            .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                            .flatMap(projectUser ->
                                                    taskStatusRepository.findById(dto.getProjectTaskStatusId())
                                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Task status not found.")))
                                                            .zipWith(
                                                                    projectRepository.findById(dto.getProjectId())
                                                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                                            )
                                                            .flatMap(tuple -> {
                                                                ProjectTaskStatus taskStatus = tuple.getT1();
                                                                Project project = tuple.getT2();

                                                                existing.setName(dto.getName());
                                                                existing.setDescription(dto.getDescription());
                                                                existing.setStartDate(dto.getStartDate());
                                                                existing.setEndDate(dto.getEndDate());
                                                                existing.setStatus(dto.getStatus());
                                                                existing.setTotalIssues(dto.getTotalIssues());
                                                                existing.setCompletedIssues(dto.getCompletedIssues());
                                                                existing.setSprintStatus(dto.getSprintStatus());
                                                                existing.setUpdatedAt(LocalDateTime.now());
                                                                existing.setCreatedProject(new CreatedProject(project));
                                                                existing.setTaskStatusOnCompletion(new TaskStatusOnCompletion(taskStatus));

                                                                return sprintRepository.save(existing);
                                                            })
                                            );
                                })
                );
    }


    public Mono<Void> addUsersToSprint(String sprintId, String projectId, List<String> userIds) {
        return projectUserRepository.findByProjectId(projectId)
                .filter(pu -> userIds.contains(pu.getUserId()))
                .map(pu -> SprintUser.builder()
                        .sprintId(sprintId)
                        .projectId(projectId)
                        .user(new CreatedBy(pu))
                        .createdProject(new CreatedProject(pu.getProjectId(), null))
                        .build())
                .collectList()
                .flatMapMany(sprintUserRepository::saveAll)
                .then();
    }




    public Mono<Void> deleteSprint(String sprintId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        sprintRepository.findById(sprintId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
                                .flatMap(existing -> {
                                    if (!existing.getCreatedBy().getId().equals(authUser.getId())) {
                                        return Mono.error(new IllegalAccessException("Only sprint creator can delete."));
                                    }
                                    return sprintUserRepository.deleteAll(
                                            sprintUserRepository.findBySprintId(sprintId)
                                    ).then(sprintRepository.deleteById(sprintId));
                                })
                );
    }

    public Flux<Sprint> getAllByProject(String projectId) {
        return sprintRepository.findAllByCreatedProjectId(projectId);
    }

    public Flux<SprintDto> getAll() {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser ->
                        sprintUserRepository.findByUserId(authUser.getId())
                                .map(SprintUser::getSprintId)
                                .distinct()
                                .collectList()
                                .flatMapMany(sprintIds -> {
                                    return sprintRepository.findAllById(sprintIds)
                                            .map(SprintDto::new);
                                })
                );
    }
    public Mono<SprintDto> getById(String sprintId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        sprintUserRepository.findBySprintIdAndUserId(sprintId, authUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not authorized to view this sprint.")))
                                .flatMap(sprintUser ->
                                        sprintRepository.findById(sprintId)
                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
                                                .map(SprintDto::new)
                                )
                );
    }
    public Flux<SprintDto> getNonCompletedSprintsByProjectId(String projectId) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser ->
                        projectUserRepository.findByProjectIdAndUserId(projectId, authUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this project.")))
                                .thenMany(
                                        sprintRepository.findAllByCreatedProjectIdAndSprintStatusIsNot(projectId, SprintStatus.COMPLATED)
                                                .map(SprintDto::new)
                                )
                );
    }
}
