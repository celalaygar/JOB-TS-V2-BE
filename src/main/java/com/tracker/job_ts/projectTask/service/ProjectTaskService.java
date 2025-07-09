package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.backlog.entity.Backlog;
import com.tracker.job_ts.backlog.repository.BacklogRepository;
import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.model.AssaignBacklog;
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
import com.tracker.job_ts.projectTask.model.ParentTask;
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

                                                    // Parent Task Kontrolü ve Set Etme
                                                    Mono<Optional<ProjectTask>> parentTaskMono = Mono.just(Optional.empty()); // Varsayılan olarak boş
                                                    if (dto.getParentTaskId() != null && !dto.getParentTaskId().isEmpty()) {
                                                        parentTaskMono = taskRepository.findById(dto.getParentTaskId())
                                                                .flatMap(parent -> {
                                                                    if (!parent.getCreatedProject().getId().equals(dto.getProjectId())) {
                                                                        return Mono.error(new IllegalArgumentException("Parent task must belong to the same project."));
                                                                    }
                                                                    return Mono.just(Optional.of(parent));
                                                                })
                                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Parent task not found with ID: " + dto.getParentTaskId()))); // Parent task bulunamazsa hata fırlat
                                                    }

                                                    return parentTaskMono.flatMap(optionalParentTask ->
                                                            taskMapper.toEntity(dto).flatMap(task -> {
                                                                task.setTaskNumber(GenerationCode.generateProjectCode(GenerationCode.TASK, dto.getTitle()));
                                                                task.setCreatedBy(createdBy);
                                                                task.setAssignee(assignee);
                                                                task.setCreatedProject(createdProject);
                                                                task.setSystemStatus(ProjectTaskSystemStatus.ACTIVE);
                                                                task.setProjectTaskStatus(new ProjectTaskStatusModel(status));
                                                                task.setCreatedAt(Instant.now().toString()); // LocalDateTime.now() daha iyi olur
                                                                task.setParentTaskId(dto.getParentTaskId()); // ParentTaskId'yi set et

                                                                // Eğer bir parent task varsa, parentTask objesini set et
                                                                optionalParentTask.ifPresent(parent ->
                                                                        task.setParentTask(new ParentTask(parent))
                                                                );

                                                                if (dto.getSprintId() != null) {
                                                                    return sprintRepository.findById(dto.getSprintId())
                                                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
                                                                            .flatMap(sprint -> {
                                                                                if (sprint.getSprintStatus() == SprintStatus.ACTIVE || sprint.getSprintStatus() == SprintStatus.PLANNED) {
                                                                                    task.setSprint(new AssaignSprint(sprint.getId(), sprint.getName()));
                                                                                    task.setBacklog(null);
                                                                                    return taskRepository.save(task).map(ProjectTaskDto::new);
                                                                                } else {
                                                                                    return Mono.error(new IllegalStateException("Sprint must be ACTIVE or PLANNED."));
                                                                                }
                                                                            });
                                                                } else {
                                                                    return ensureBacklog(dto.getProjectId(), createdBy, createdProject)
                                                                            .flatMap(backlog -> {
                                                                                task.setSprint(null);
                                                                                task.setBacklog(new AssaignBacklog(backlog.getId(), backlog.getName()));
                                                                                return taskRepository.save(task).map(ProjectTaskDto::new);
                                                                            });
                                                                }
                                                            })
                                                    );
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

                                                            // Parent Task Kontrolü ve Set Etme
                                                            Mono<Optional<ProjectTask>> parentTaskMono = Mono.just(Optional.empty()); // Varsayılan olarak boş
                                                            if (dto.getParentTaskId() != null && !dto.getParentTaskId().isEmpty()) {
                                                                // Kendini parent olarak atamaya çalışma kontrolü
                                                                if (dto.getParentTaskId().equals(existingTask.getId())) {
                                                                    return Mono.error(new IllegalArgumentException("A task cannot be its own parent."));
                                                                }
                                                                parentTaskMono = taskRepository.findById(dto.getParentTaskId())
                                                                        .flatMap(parent -> {
                                                                            if (!parent.getCreatedProject().getId().equals(dto.getProjectId())) {
                                                                                return Mono.error(new IllegalArgumentException("Parent task must belong to the same project."));
                                                                            }
                                                                            return Mono.just(Optional.of(parent));
                                                                        })
                                                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Parent task not found with ID: " + dto.getParentTaskId()))); // Parent task bulunamazsa hata fırlat
                                                            }


                                                            return parentTaskMono.flatMap(optionalParentTask -> Mono.defer(() -> {
                                                                existingTask.setTitle(dto.getTitle());
                                                                existingTask.setDescription(dto.getDescription());
                                                                existingTask.setPriority(dto.getPriority());
                                                                existingTask.setTaskType(dto.getTaskType());
                                                                existingTask.setParentTaskId(dto.getParentTaskId()); // ParentTaskId'yi set et
                                                                existingTask.setAssignee(assignee);
                                                                existingTask.setCreatedProject(createdProject);
                                                                existingTask.setCreatedBy(createdBy);
                                                                existingTask.setProjectTaskStatus(new ProjectTaskStatusModel(status));
                                                                existingTask.setSystemStatus(ProjectTaskSystemStatus.ACTIVE);

                                                                // Eğer bir parent task varsa, parentTask objesini set et
                                                                // Eğer parentTaskId null/boş ise, parentTask alanını da null yap
                                                                if (optionalParentTask.isPresent()) {
                                                                    existingTask.setParentTask(new ParentTask(optionalParentTask.get()));
                                                                } else if (dto.getParentTaskId() == null || dto.getParentTaskId().isEmpty()) {
                                                                    existingTask.setParentTask(null);
                                                                }


                                                                if (dto.getSprintId() != null) {
                                                                    return sprintRepository.findById(dto.getSprintId())
                                                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
                                                                            .flatMap(sprint -> {
                                                                                if (sprint.getSprintStatus() == SprintStatus.ACTIVE || sprint.getSprintStatus() == SprintStatus.PLANNED) {
                                                                                    existingTask.setSprint(new AssaignSprint(sprint.getId(), sprint.getName()));
                                                                                    existingTask.setBacklog(null);
                                                                                    return taskRepository.save(existingTask).map(ProjectTaskDto::new);
                                                                                } else {
                                                                                    return Mono.error(new IllegalStateException("Sprint must be ACTIVE or PLANNED."));
                                                                                }
                                                                            });
                                                                } else {
                                                                    return ensureBacklog(dto.getProjectId(), createdBy, createdProject)
                                                                            .flatMap(backlog -> {
                                                                                existingTask.setBacklog(new AssaignBacklog(backlog.getId(), backlog.getName()));
                                                                                existingTask.setSprint(null);
                                                                                return taskRepository.save(existingTask).map(ProjectTaskDto::new);
                                                                            });
                                                                }
                                                            }));
                                                        })
                                                )
                                        )
                                )
                        )
                );
    }


    public Mono<ProjectTaskDto> updateParentTask(String taskId, ProjectTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> taskRepository.findById(taskId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Task not found with ID: " + taskId)))
                        .flatMap(existingTask -> projectUserRepository.findByProjectIdAndUserId(existingTask.getCreatedProject().getId(), user.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of the project this task belongs to.")))
                                .flatMap(projectUser -> {
                                    Mono<Optional<ProjectTask>> parentTaskMono = Mono.just(Optional.empty());

                                    if (dto.getParentTaskId() != null && !dto.getParentTaskId().isEmpty()) {
                                        // Kendini parent olarak atamaya çalışma kontrolü
                                        if (dto.getParentTaskId().equals(existingTask.getId())) {
                                            return Mono.error(new IllegalArgumentException("A task cannot be its own parent."));
                                        }

                                        parentTaskMono = taskRepository.findById(dto.getParentTaskId())
                                                .flatMap(parentTask -> {
                                                    if (!parentTask.getCreatedProject().getId().equals(existingTask.getCreatedProject().getId())) { // Proje kontrolü
                                                        return Mono.error(new IllegalArgumentException("Parent task must belong to the same project."));
                                                    }
                                                    return Mono.just(Optional.of(parentTask));
                                                })
                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Parent task not found with ID: " + dto.getParentTaskId())));
                                    }

                                    return parentTaskMono.flatMap(optionalParentTask -> Mono.defer(() -> {
                                        existingTask.setParentTaskId(dto.getParentTaskId());
                                        // Eğer bir parent task varsa, parentTask objesini set et
                                        // Eğer parentTaskId null/boş ise, parentTask alanını da null yap
                                        if (optionalParentTask.isPresent()) {
                                            existingTask.setParentTask(new ParentTask(optionalParentTask.get()));
                                        } else if (dto.getParentTaskId() == null || dto.getParentTaskId().isEmpty()) {
                                            existingTask.setParentTask(null);
                                        }
                                        return taskRepository.save(existingTask).map(ProjectTaskDto::new);
                                    }));
                                })
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
    /**
     * Belirli bir parent task ID'sine sahip tüm alt görevleri getirir.
     * Kullanıcının bu görevlerin ait olduğu projelere erişim yetkisi olmalıdır.
     *
     * @param parentTaskId Alt görevleri getirilecek olan parent görevin ID'si
     * @return Belirtilen parent task'a ait alt görevlerin bir listesi (ProjectTaskDto olarak)
     */
    public Flux<ProjectTaskDto> getSubtasksByParentTaskId(String parentTaskId) {
        return authHelperService.getAuthUser()
                .flatMapMany(user -> taskRepository.findById(parentTaskId) // Parent task'ın varlığını ve projeyi bul
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Parent task not found with ID: " + parentTaskId)))
                        .flatMapMany(parentTask -> projectUserRepository.findByProjectIdAndUserId(parentTask.getCreatedProject().getId(), user.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of the project this parent task belongs to.")))
                                .flatMapMany(projectUser -> {
                                    // parentTaskId alanına göre filtreleme yaparak alt görevleri bul
                                    return taskRepository.findByParentTaskId(parentTaskId)
                                            .map(ProjectTaskDto::new);
                                })
                        )
                );
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
