package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.backlog.entity.Backlog;
import com.tracker.job_ts.backlog.repository.BacklogRepository;
import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.model.AssaignSprint;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTaskStatusRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.dto.ProjectTaskFltreRequestDto;
import com.tracker.job_ts.projectTask.dto.ProjectTaskRequestDto;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import com.tracker.job_ts.projectTask.model.ProjectTaskStatusModel;
import com.tracker.job_ts.projectTask.model.ProjectTaskSystemStatus;
import com.tracker.job_ts.projectTask.repository.ProjectTaskRepository;
import com.tracker.job_ts.sprint.entity.SprintStatus;
import com.tracker.job_ts.sprint.repository.SprintRepository;
import com.tracker.job_ts.sprint.util.GenerationCode;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<ProjectTaskDto> createTask(ProjectTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), user.getId())
                        .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                        .flatMap(projectUser -> projectRepository.findById(dto.getProjectId())
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                .flatMap(project -> projectTaskStatusRepository.findByIdAndCreatedProjectId(dto.getProjectTaskStatusId(), dto.getProjectId())
                                        .switchIfEmpty(Mono.error(new NoSuchElementException("ProjectTaskStatus not found.")))
                                        .flatMap(status -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), dto.getAssigneeId())
                                                .switchIfEmpty(Mono.error(new IllegalArgumentException("Assignee is not a member of this project.")))
                                                .flatMap(assigneeProjectUser -> {
                                                    CreatedBy assignee = new CreatedBy(assigneeProjectUser);
                                                    CreatedProject createdProject = new CreatedProject(project);
                                                    CreatedBy createdBy = new CreatedBy(user);

                                                    return taskMapper.toEntity(dto).flatMap(task -> {
                                                        task.setTaskNumber(GenerationCode.generateProjectCode(GenerationCode.TASK, dto.getTitle()));
                                                        task.setCreatedBy(createdBy);
                                                        task.setAssignee(assignee);
                                                        task.setCreatedProject(createdProject);
                                                        task.setSystemStatus(ProjectTaskSystemStatus.ACTIVE);
                                                        task.setProjectTaskStatus(new ProjectTaskStatusModel(status));
                                                        task.setCreatedAt(Instant.now().toString());

                                                        if (dto.getSprintId() != null) {
                                                            return sprintRepository.findById(dto.getSprintId())
                                                                    .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
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
                                                    });
                                                })
                                        )
                                )
                        )
                );
    }

    public Mono<ProjectTaskDto> updateTask(String taskId, ProjectTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> taskRepository.findById(taskId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Task not found.")))
                        .flatMap(existingTask -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), user.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                .flatMap(projectUser -> projectRepository.findById(dto.getProjectId())
                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found.")))
                                        .flatMap(project -> projectTaskStatusRepository.findByIdAndCreatedProjectId(dto.getProjectTaskStatusId(), dto.getProjectId())
                                                .switchIfEmpty(Mono.error(new NoSuchElementException("ProjectTaskStatus not found.")))
                                                .flatMap(status -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), dto.getAssigneeId())
                                                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Assignee is not a member of this project.")))
                                                        .flatMap(assigneeProjectUser -> {
                                                            CreatedBy assignee = new CreatedBy(assigneeProjectUser);
                                                            CreatedProject createdProject = new CreatedProject(project);
                                                            CreatedBy createdBy = new CreatedBy(user);

                                                            existingTask.setTitle(dto.getTitle());
                                                            existingTask.setDescription(dto.getDescription());
                                                            existingTask.setPriority(dto.getPriority());
                                                            existingTask.setTaskType(dto.getTaskType());
                                                            existingTask.setParentTaskId(dto.getParentTaskId());
                                                            existingTask.setAssignee(assignee);
                                                            existingTask.setCreatedProject(createdProject);
                                                            existingTask.setCreatedBy(createdBy);
                                                            existingTask.setProjectTaskStatus(new ProjectTaskStatusModel(status));
                                                            existingTask.setSystemStatus(ProjectTaskSystemStatus.ACTIVE);

                                                            if (dto.getSprintId() != null) {
                                                                return sprintRepository.findById(dto.getSprintId())
                                                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
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
                                                )
                                        )
                                )
                        )
                );
    }

    public Mono<ProjectTaskDto> getByTaskId(String projectTaskId) {
        return authHelperService.getAuthUser()
                .flatMap(user -> projectUserRepository.findAllByUserId(user.getId()) // Flux<ProjectUser>
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project User found.")))
                        .flatMap(projectUser -> projectRepository.findById(projectUser.getProjectId()))
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project found.")))
                        .map(Project::getId)
                        .collectList() // Mono<List<String>> tüm proje ID’leri
                        .flatMap(projectIds -> taskRepository.findById(projectTaskId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Project User found.")))
                                .flatMap(existingTask ->
                                        projectIds.contains(existingTask.getCreatedProject().getId()) ?
                                                Mono.just(new ProjectTaskDto(existingTask)) :
                                                Mono.error(new NoSuchElementException("Task not found."))
                                )
                        )
                );
    }

    public Mono<PagedResult<ProjectTaskDto>> getAllFilteredTasks(ProjectTaskFltreRequestDto filterDto, int page, int size) {
        return authHelperService.getAuthUser()
                .flatMap(user -> projectUserRepository.findAllByUserId(user.getId()) // Flux<ProjectUser>
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project User found.")))
                        .flatMap(projectUser -> projectRepository.findById(projectUser.getProjectId()))
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project found.")))
                        .map(Project::getId)
                        .collectList() // Mono<List<String>> tüm proje ID’leri
                        .flatMap(projectIds -> this.filterTasks(projectIds, filterDto, page, size))
                );
    }

    public Mono<PagedResult<ProjectTaskDto>> filterTasks(List<String> projectIds, ProjectTaskFltreRequestDto filterDto, int page, int size) {
        Query query = new Query();

        if (!StringUtils.isEmpty(filterDto.getProjectId()) && !CollectionUtils.isEmpty(projectIds) &&
                projectIds.contains(filterDto.getProjectId()) && !StringUtils.isEmpty(filterDto.getProjectTaskStatusId())) {
            query.addCriteria(
                    Criteria.where("projectTaskStatus.id").is(filterDto.getProjectTaskStatusId())
                            .and("createdProject.id").is(filterDto.getProjectId()));
        } else if (filterDto.getProjectId() != null &&
                !CollectionUtils.isEmpty(projectIds) &&
                projectIds.contains(filterDto.getProjectId()) &&
                !StringUtils.isEmpty(filterDto.getProjectId())) {
            query.addCriteria(Criteria.where("createdProject.id").is(filterDto.getProjectId()));
        } else if (!CollectionUtils.isEmpty(projectIds)) {
            query.addCriteria(Criteria.where("createdProject.id").in(projectIds));
        }

        if (!StringUtils.isEmpty(filterDto.getTitle())) {
            query.addCriteria(Criteria.where("title").regex(filterDto.getTitle(), "i"));
        }
        if (!StringUtils.isEmpty(filterDto.getDescription())) {
            query.addCriteria(Criteria.where("description").regex(filterDto.getDescription(), "i"));
        }
        if (filterDto.getPriority() != null) {
            query.addCriteria(Criteria.where("priority").is(filterDto.getPriority()));
        }
        if (filterDto.getTaskType() != null) {
            query.addCriteria(Criteria.where("taskType").is(filterDto.getTaskType()));
        }

        if (!StringUtils.isEmpty(filterDto.getAssigneeId())) {
            query.addCriteria(Criteria.where("assignee.id").is(filterDto.getAssigneeId()));
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        query.with(pageRequest);

        Mono<List<ProjectTaskDto>> tasks = mongoTemplate.find(query, ProjectTask.class).map(ProjectTaskDto::new).collectList();
        Mono<Long> count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ProjectTask.class);

        return Mono.zip(tasks, count)
                .map(tuple -> new PagedResult<ProjectTaskDto>(tuple.getT1(), tuple.getT2(), page, size));
    }

/*
    public Mono<PagedResult<ProjectTaskDto>> filterTasks(ProjectTaskFltreRequestDto filterDto, int page, int size) {
        Query query = new Query();
        if (!StringUtils.isEmpty(filterDto.getTitle())) {
            query.addCriteria(Criteria.where("title").regex(filterDto.getTitle(), "i"));
        }
        if (!StringUtils.isEmpty(filterDto.getDescription())) {
            query.addCriteria(Criteria.where("description").regex(filterDto.getDescription(), "i"));
        }
        if (filterDto.getPriority() != null) {
            query.addCriteria(Criteria.where("priority").is(filterDto.getPriority()));
        }
        if (filterDto.getTaskType() != null) {
            query.addCriteria(Criteria.where("taskType").is(filterDto.getTaskType()));
        }
        if (!StringUtils.isEmpty(filterDto.getProjectId())) {
            query.addCriteria(Criteria.where("createdProject.id").is(filterDto.getProjectId()));
        }
        if (!StringUtils.isEmpty(filterDto.getProjectId()) && !StringUtils.isEmpty(filterDto.getProjectTaskStatusId())) {
            query.addCriteria(Criteria.where("projectTaskStatus.id").is(filterDto.getProjectTaskStatusId()).and("createdProject.id").is(filterDto.getProjectId()));
        }
        if (!StringUtils.isEmpty(filterDto.getAssigneeId())) {
            query.addCriteria(Criteria.where("assignee.id").is(filterDto.getAssigneeId()));
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        query.with(pageRequest);
        Mono<List<ProjectTaskDto>> tasks = mongoTemplate.find(query, ProjectTask.class).map(ProjectTaskDto::new).collectList();
        Mono<Long> count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ProjectTask.class);
        return Mono.zip(tasks, count)
                .map(tuple -> new PagedResult<ProjectTaskDto>(tuple.getT1(), tuple.getT2(), page, size));
    }
*/

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
