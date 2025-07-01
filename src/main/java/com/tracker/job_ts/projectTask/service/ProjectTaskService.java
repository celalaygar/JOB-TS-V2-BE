package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.backlog.entity.Backlog;
import com.tracker.job_ts.backlog.repository.BacklogRepository;
import com.tracker.job_ts.project.model.AssaignSprint;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTaskStatusRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.dto.ProjectTaskRequestDto;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import com.tracker.job_ts.projectTask.model.ProjectTaskStatusModel;
import com.tracker.job_ts.projectTask.model.ProjectTaskSystemStatus;
import com.tracker.job_ts.projectTask.repository.ProjectTaskRepository;
import com.tracker.job_ts.sprint.entity.SprintStatus;
import com.tracker.job_ts.sprint.repository.SprintRepository;
import com.tracker.job_ts.sprint.util.GenerationCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final ProjectTaskRepository taskRepository;
    private final ProjectTaskStatusRepository projectTaskStatusRepository;
    private final ProjectTaskMapper taskMapper;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final SprintRepository sprintRepository;
    private final BacklogRepository backlogRepository;
    private final AuthHelperService authHelperService;

    public Mono<ProjectTaskDto> createTask(ProjectTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), user.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalAccessException("User is not a member of this project."))))
                        .flatMap(projectUser -> projectRepository.findById(dto.getProjectId())
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("Project not found."))))
                                .flatMap(project ->
                                        projectTaskStatusRepository.findByIdAndCreatedProjectId(dto.getProjectTaskStatusId(), dto.getProjectId())
                                                .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("ProjectTaskStatus not found."))))
                                                .flatMap(status -> taskMapper.toEntity(dto).flatMap(task -> {
                                                    CreatedProject createdProject = new CreatedProject(project);
                                                    CreatedBy createdBy = new CreatedBy(user);
                                                    task.setTaskNumber(GenerationCode.generateProjectCode(GenerationCode.TASK, dto.getTitle()));
                                                    task.setCreatedBy(createdBy);
                                                    task.setAssignee(dto.getAssignee());
                                                    task.setCreatedProject(createdProject);
                                                    task.setSystemStatus(ProjectTaskSystemStatus.ACTIVE);
                                                    task.setProjectTaskStatus(new ProjectTaskStatusModel(status));
                                                    task.setCreatedAt(Instant.now().toString());

                                                    if (dto.getSprintId() != null) {
                                                        return sprintRepository.findById(dto.getSprintId())
                                                                .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("Sprint not found."))))
                                                                .flatMap(sprint -> {
                                                                    if (sprint.getSprintStatus() == SprintStatus.ACTIVE || sprint.getSprintStatus() == SprintStatus.PLANNED) {
                                                                        task.setSprint(new AssaignSprint(sprint.getId(), sprint.getName()));
                                                                        return taskRepository.save(task).map(ProjectTaskDto::new);
                                                                    } else {
                                                                        return Mono.error(new IllegalStateException("Sprint must be ACTIVE or PLANNED."));
                                                                    }
                                                                });
                                                    } else {
                                                        return ensureBacklog(dto.getProjectId(), createdBy, createdProject)
                                                                .flatMap(backlog -> {
                                                                    task.setSprint(new AssaignSprint(backlog.getId(), backlog.getName()));
                                                                    return taskRepository.save(task).map(ProjectTaskDto::new);
                                                                });
                                                    }
                                                }))
                                )
                        )
                );
    }

    public Mono<ProjectTaskDto> updateTask(String taskId, ProjectTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> taskRepository.findById(taskId)
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("Task not found."))))
                        .flatMap(existingTask -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), user.getId())
                                .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalAccessException("User is not a member of this project."))))
                                .flatMap(projectUser -> projectRepository.findById(dto.getProjectId())
                                        .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("Project not found."))))
                                        .flatMap(project ->
                                                projectTaskStatusRepository.findByIdAndCreatedProjectId(dto.getProjectTaskStatusId(), dto.getProjectId())
                                                        .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("ProjectTaskStatus not found."))))
                                                        .flatMap(status -> {
                                                            CreatedProject createdProject = new CreatedProject(project);
                                                            CreatedBy createdBy = new CreatedBy(user);
                                                            existingTask.setTitle(dto.getTitle());
                                                            existingTask.setDescription(dto.getDescription());
                                                            existingTask.setPriority(dto.getPriority());
                                                            existingTask.setTaskType(dto.getTaskType());
                                                            existingTask.setParentTaskId(dto.getParentTaskId());
                                                            existingTask.setAssignee(dto.getAssignee());
                                                            existingTask.setCreatedProject(createdProject);
                                                            existingTask.setCreatedBy(createdBy);
                                                            existingTask.setProjectTaskStatus(new ProjectTaskStatusModel(status));
                                                            existingTask.setSystemStatus(ProjectTaskSystemStatus.ACTIVE);

                                                            if (dto.getSprintId() != null) {
                                                                return sprintRepository.findById(dto.getSprintId())
                                                                        .switchIfEmpty(Mono.defer(() -> Mono.error(new NoSuchElementException("Sprint not found."))))
                                                                        .flatMap(sprint -> {
                                                                            if (sprint.getSprintStatus() == SprintStatus.ACTIVE || sprint.getSprintStatus() == SprintStatus.PLANNED) {
                                                                                existingTask.setSprint(new AssaignSprint(sprint.getId(), sprint.getName()));
                                                                                return taskRepository.save(existingTask).map(ProjectTaskDto::new);
                                                                            } else {
                                                                                return Mono.error(new IllegalStateException("Sprint must be ACTIVE or PLANNED."));
                                                                            }
                                                                        });
                                                            } else {
                                                                return ensureBacklog(dto.getProjectId(), createdBy, createdProject)
                                                                        .flatMap(backlog -> {
                                                                            existingTask.setSprint(new AssaignSprint(backlog.getId(), backlog.getName()));
                                                                            return taskRepository.save(existingTask).map(ProjectTaskDto::new);
                                                                        });
                                                            }
                                                        })
                                        ))
                        )
                );
    }

    public Flux<ProjectTaskDto> getAllByProjectId(String projectId) {
        return authHelperService.getAuthUser()
                .flatMapMany(user -> projectUserRepository.findByProjectIdAndUserId(projectId, user.getId())
                        .switchIfEmpty(Mono.defer(() -> Mono.error(new IllegalAccessException("User is not a member of this project."))))
                        .thenMany(taskRepository.findByCreatedProjectId(projectId)
                                .map(ProjectTaskDto::new))
                );
    }

    private Mono<Backlog> ensureBacklog(String projectId, CreatedBy user, CreatedProject project) {
        return backlogRepository.findByCreatedProjectId(projectId)
                .switchIfEmpty(
                        backlogRepository.save(
                                Backlog.builder()
                                        .name("Backlog")
                                        .description("Auto generated backlog")
                                        .createdProject(project)
                                        .createdBy(user)
                                        .createdAt(LocalDateTime.now())
                                        .updatedAt(LocalDateTime.now())
                                        .build()
                        )
                );
    }
}
