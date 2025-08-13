package com.tracker.job_ts.sprint.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectTaskStatus;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.*;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTaskStatusRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import com.tracker.job_ts.projectTask.repository.ProjectTaskRepository;
import com.tracker.job_ts.sprint.dto.SprintDto;
import com.tracker.job_ts.sprint.dto.SprintRegisterDto;
import com.tracker.job_ts.sprint.dto.SprintStatusUpdateRequestDto;
import com.tracker.job_ts.sprint.dto.SprintTaskRequestDto;
import com.tracker.job_ts.sprint.entity.Sprint;
import com.tracker.job_ts.sprint.entity.SprintStatus;
import com.tracker.job_ts.sprint.entity.SprintUser;
import com.tracker.job_ts.sprint.model.TaskStatusOnCompletion;
import com.tracker.job_ts.sprint.repository.SprintRepository;
import com.tracker.job_ts.sprint.repository.SprintUserRepository;
import com.tracker.job_ts.sprint.util.GenerationCode;
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
public class SprintService {
    private final SprintRepository sprintRepository;
    private final SprintUserRepository sprintUserRepository;
    private final ProjectUserRepository projectUserRepository;
    private final ProjectTaskStatusRepository taskStatusRepository;
    private final AuthHelperService authHelperService;
    private final SprintValidator validator;
    private final ProjectTaskRepository projectTaskRepository;

    private final ProjectRepository projectRepository;

    public Mono<Sprint> createSprint(SprintRegisterDto dto) {
        validator.validate(dto);

        return authHelperService.getAuthUser()
                .flatMap(authUser ->
                        projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                dto.getProjectId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
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

                                    return projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                            dto.getProjectId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
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
                        projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                projectId, authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this project.")))
                                .thenMany(
                                        sprintRepository.findAllByCreatedProjectIdAndSprintStatusIsNot(projectId, SprintStatus.COMPLATED)
                                                .map(SprintDto::new)
                                )
                );
    }

    /**
     * Belirli bir sprint ve projeye ait tüm görevleri getirir.
     * Kullanıcının ilgili projenin ve sprint'in üyesi olması gerekmektedir.
     *
     * @param sprintId  Görevleri getirilecek sprint'in ID'si
     * @param projectId Görevlerin ait olduğu projenin ID'si
     * @return Sprint'e atanmış görevlerin DTO'ları (Flux olarak)
     */
    public Flux<ProjectTaskDto> getProjectTasksBySprintAndProject(SprintTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMapMany(currentUser ->
                        // 1. Kullanıcının projenin bir üyesi olduğunu doğrula
                        projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                dto.getProjectId(), currentUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this project.")))
                                .flatMapMany(projectUser ->
                                        // 2. Kullanıcının aynı zamanda sprint'in bir üyesi olduğunu doğrula
                                        sprintUserRepository.findBySprintIdAndUserId(dto.getSprintId(), currentUser.getId())
                                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this sprint.")))
                                                .flatMapMany(sprintUser ->
                                                        // 3. Sprint'in varlığını doğrula (ve projenin sprint'e ait olduğunu kontrol et)
                                                        sprintRepository.findById(dto.getSprintId())
                                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + dto.getSprintId())))
                                                                .flatMapMany(sprint -> {
                                                                    if (!sprint.getCreatedProject().getId().equals(dto.getProjectId())) {
                                                                        return Mono.error(new IllegalArgumentException("Sprint with ID " + dto.getSprintId() + " does not belong to project with ID " + dto.getProjectId()));
                                                                    }
                                                                    // 4. Sprint'e ve projeye atanmış görevleri bul
                                                                    return projectTaskRepository.findBySprintIdAndCreatedProjectId(dto.getSprintId(), dto.getProjectId()).map(ProjectTaskDto::new);
                                                                })
                                                )
                                )
                );
    }

    /**
     * Belirli bir görevi sprint'e atar.
     * Kullanıcının ilgili projenin ve sprint'in üyesi olması ve görevin projeye ait olması gerekmektedir.
     *
     * @param sprintId  Görevin atanacağı sprint'in ID'si
     * @param projectId Görevin ait olduğu projenin ID'si
     * @param taskId    Atanacak görevin ID'si
     * @return Güncellenmiş ProjectTask
     */
    public Mono<ProjectTaskDto> addTaskToSprint(SprintTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser ->
                        // 1. Kullanıcının projenin bir üyesi olduğunu doğrula
                        projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                dto.getProjectId(), currentUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this project.")))
                                .flatMap(projectUser ->
                                        // 2. Kullanıcının aynı zamanda sprint'in bir üyesi olduğunu doğrula
                                        sprintUserRepository.findBySprintIdAndUserId(dto.getSprintId(), currentUser.getId())
                                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this sprint.")))
                                                .flatMap(sprintUser ->
                                                        // 3. Sprint'in varlığını doğrula
                                                        sprintRepository.findById(dto.getSprintId())
                                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + dto.getSprintId())))
                                                                .flatMap(sprint -> {
                                                                    // Sprint'in ilgili projeye ait olduğunu kontrol et
                                                                    if (!sprint.getCreatedProject().getId().equals(dto.getProjectId())) {
                                                                        return Mono.error(new IllegalArgumentException("Sprint with ID " + dto.getSprintId() + " does not belong to project with ID " + dto.getProjectId()));
                                                                    }
                                                                    // 4. Görevin varlığını ve ilgili projeye ait olduğunu doğrula
                                                                    return projectTaskRepository.findById(dto.getTaskId())
                                                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Project Task not found with ID: " + dto.getTaskId())))
                                                                            .flatMap(task -> {
                                                                                if (!task.getCreatedProject().getId().equals(dto.getProjectId())) {
                                                                                    return Mono.error(new IllegalArgumentException("Task with ID " + dto.getTaskId() + " does not belong to project with ID " + dto.getProjectId()));
                                                                                }
                                                                                // 5. Görev zaten bu sprint'e atanmış mı kontrol et
                                                                                if (task.getSprint() != null && task.getSprint().getId().equals(dto.getSprintId())) {
                                                                                    return Mono.error(new IllegalArgumentException("Task with ID " + dto.getTaskId() + " is already assigned to sprint " + dto.getSprintId()));
                                                                                }
                                                                                // 6. Görevi sprint'e ata ve kaydet
                                                                                task.setSprint(new AssaignSprint(sprint.getId(), sprint.getName()));
                                                                                return projectTaskRepository.save(task).map(ProjectTaskDto::new);
                                                                            });
                                                                })
                                                )
                                )
                );
    }

    /**
     * Belirli bir görevi sprint'ten çıkartır.
     * Kullanıcının ilgili projenin ve sprint'in üyesi olması ve görevin projeye ait olması gerekmektedir.
     *
     * @param dto.getTaskId()  Görevin çıkarılacağı sprint'in ID'si
     * @param projectId Görevin ait olduğu projenin ID'si
     * @param taskId    Çıkartılacak görevin ID'si
     * @return Güncellenmiş ProjectTask
     */
    public Mono<ProjectTaskDto> removeTaskFromSprint(SprintTaskRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser ->
                        // 1. Kullanıcının projenin bir üyesi olduğunu doğrula
                        projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                dto.getProjectId(), currentUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this project.")))
                                .flatMap(projectUser ->
                                        // 2. Kullanıcının aynı zamanda sprint'in bir üyesi olduğunu doğrula
                                        sprintUserRepository.findBySprintIdAndUserId(dto.getSprintId(), currentUser.getId())
                                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not a member of this sprint.")))
                                                .flatMap(sprintUser ->
                                                        // 3. Sprint'in varlığını doğrula
                                                        sprintRepository.findById(dto.getSprintId())
                                                                .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + dto.getSprintId())))
                                                                .flatMap(sprint -> {
                                                                    // Sprint'in ilgili projeye ait olduğunu kontrol et
                                                                    if (!sprint.getCreatedProject().getId().equals(dto.getProjectId())) {
                                                                        return Mono.error(new IllegalArgumentException("Sprint with ID " + dto.getSprintId() + " does not belong to project with ID " + dto.getProjectId()));
                                                                    }
                                                                    // 4. Görevin varlığını ve ilgili projeye ve sprint'e ait olduğunu doğrula
                                                                    return projectTaskRepository.findByIdAndSprintIdAndCreatedProjectId(dto.getTaskId(), dto.getSprintId(), dto.getProjectId())
                                                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Project Task with ID " + dto.getTaskId() + " not found in sprint " + dto.getSprintId() + " or project " + dto.getProjectId())))
                                                                            .flatMap(task -> {
                                                                                // 5. Görevi sprint'ten çıkar (AssaignSprint alanını null yap)
                                                                                task.setSprint(null);
                                                                                return projectTaskRepository.save(task).map(ProjectTaskDto::new);
                                                                            });
                                                                })
                                                )
                                )
                );
    }
    /**
     * Bir sprint'in durumunu PLANNED veya ACTIVE olarak günceller.
     * Bu işlemi sadece PROJECT_ADMIN veya PROJECT_OWNER rolüne sahip kullanıcılar yapabilir.
     *
     * @param dto Yeni sprint durumunu ve sprint ID'sini içeren DTO
     * @return Güncellenmiş Sprint nesnesi
     */
    public Mono<SprintDto> updateSprintStatus(SprintStatusUpdateRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> sprintRepository.findById(dto.getSprintId())
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found.")))
                        .flatMap(sprint ->projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(
                                        sprint.getCreatedProject().getId(), authUser.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                                        .switchIfEmpty(Mono.error(new AccessDeniedException("Only project admins or owners can change sprint status.")))
                                        .flatMap(projectUser -> {
                                            SprintStatus newStatus = dto.getNewStatus();

                                            // Durum geçişi kontrolleri
                                            if (newStatus == SprintStatus.ACTIVE) {
                                                if (sprint.getSprintStatus() != SprintStatus.PLANNED) {
                                                    return Mono.error(new IllegalStateException("Sprint must be in PLANNED status to be activated."));
                                                }
                                            } else if (newStatus == SprintStatus.PLANNED) {
                                                if (sprint.getSprintStatus() != SprintStatus.ACTIVE) {
                                                    return Mono.error(new IllegalStateException("Sprint must be in ACTIVE status to be planned."));
                                                }
                                            } else {
                                                // Diğer durumlar için (örn. COMPLETED) geçersiz geçiş hatası ver
                                                return Mono.error(new IllegalArgumentException("Invalid sprint status transition."));
                                            }

                                            // Durumu güncelle ve kaydet
                                            sprint.setSprintStatus(newStatus);
                                            sprint.setUpdatedAt(LocalDateTime.now());
                                            return sprintRepository.save(sprint).map(SprintDto::new);
                                        })
                        )
                );
    }
}
