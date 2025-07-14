package com.tracker.job_ts.sprint.service;
// package com.tracker.job_ts.sprint.service; (Dosya yolu önerisi)


import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.entity.Project;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.sprint.dto.SprintUserBulkRequestDto;
import com.tracker.job_ts.sprint.dto.SprintUserDto;
import com.tracker.job_ts.sprint.dto.SprintUserRequestDto;
import com.tracker.job_ts.sprint.entity.Sprint;
import com.tracker.job_ts.sprint.entity.SprintUser;
import com.tracker.job_ts.sprint.repository.SprintRepository;
import com.tracker.job_ts.sprint.repository.SprintUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SprintUserService {

    private final SprintUserRepository sprintUserRepository;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final AuthHelperService authHelperService;

    /**
     * Sprint'e tek bir proje kullanıcısı ekler.
     * Kullanıcının ilgili projeye üye olması gerekmektedir.
     * @param dto Eklenecek kullanıcı bilgileri (sprintId, projectId, userId vb.)
     * @return Eklenen SprintUser'ın DTO'su
     */
    public Mono<SprintUserDto> addProjectUserToSprint(SprintUserRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser -> projectRepository.findById(dto.getProjectId())
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found with ID: " + dto.getProjectId()))) // Tipi açıkça belirtildi
                        .flatMap(project -> projectUserRepository.findByProjectIdAndUserId(project.getId(), currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("Current user is not a member of project " + dto.getProjectId()))) // Tipi açıkça belirtildi
                                .flatMap(actingProjectUser -> sprintRepository.findById(dto.getSprintId())
                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + dto.getSprintId()))) // Tipi açıkça belirtildi
                                        .flatMap(sprint -> projectUserRepository.findByProjectIdAndUserId(project.getId(), dto.getUserId())
                                                .switchIfEmpty(Mono.error(new NoSuchElementException("User with ID " + dto.getUserId() + " is not a member of project " + dto.getProjectId()))) // Tipi açıkça belirtildi
                                                .flatMap(userToAddAsProjectUser -> sprintUserRepository.findBySprintIdAndUserId(dto.getSprintId(), dto.getUserId())
                                                        .flatMap(existingSprintUser -> Mono.<SprintUserDto>error(new IllegalArgumentException("User with ID " + dto.getUserId() + " is already assigned to sprint " + dto.getSprintId()))) // En kritik nokta burasıydı!
                                                        .switchIfEmpty(Mono.defer(() -> {
                                                            SprintUser sprintUser = SprintUser.builder()
                                                                    .sprintId(dto.getSprintId())
                                                                    .projectId(dto.getProjectId())
                                                                    .user(new CreatedBy(userToAddAsProjectUser))
                                                                    .createdProject(new CreatedProject(project))
                                                                    .assignmentDate(Instant.now())
                                                                    .roleInSprint(dto.getRoleInSprint() != null ? dto.getRoleInSprint() : "Member")
                                                                    .statusInSprint(dto.getStatusInSprint() != null ? dto.getStatusInSprint() : "Active")
                                                                    .estimatedEffort(dto.getEstimatedEffort())
                                                                    .notes(dto.getNotes())
                                                                    .createdAt(LocalDateTime.now())
                                                                    .updatedAt(LocalDateTime.now())
                                                                    .build();
                                                            return sprintUserRepository.save(sprintUser).map(SprintUserDto::new);
                                                        }))
                                                )
                                        )
                                )
                        )
                );
    }

    /**
     * Sprint'ten tek bir kullanıcıyı çıkartır.
     * @param sprintId Kullanıcının çıkartılacağı sprint'in ID'si
     * @param userId Çıkartılacak kullanıcının ID'si
     * @return Başarı durumunda boş Mono
     */
    public Mono<Void> removeProjectUserFromSprint(String sprintId, String userId) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser -> sprintUserRepository.findBySprintIdAndUserId(sprintId, userId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("User with ID " + userId + " not found in sprint " + sprintId)))
                        .flatMap(sprintUser -> projectUserRepository.findByProjectIdAndUserId(sprintUser.getProjectId(), currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("Current user is not a member of project " + sprintUser.getProjectId())))
                                .flatMap(actingProjectUser -> sprintUserRepository.delete(sprintUser))
                        )
                );
    }

    /**
     * Sprint'e birden fazla proje kullanıcısını toplu olarak ekler.
     * Her kullanıcı için tek tek yetki ve varlık kontrolü yapar.
     * Var olmayan kullanıcıları veya zaten sprintte olanları atlar.
     * @param dto Eklenecek kullanıcı ID'leri ve diğer bilgiler
     * @return Eklenen SprintUser'ların DTO'ları (Flux olarak)
     */
    public Flux<SprintUserDto> addBulkProjectUsersToSprint(SprintUserBulkRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMapMany(currentUser -> projectRepository.findById(dto.getProjectId())
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found with ID: " + dto.getProjectId())))
                        .flatMapMany(project -> projectUserRepository.findByProjectIdAndUserId(project.getId(), currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("Current user is not a member of project " + dto.getProjectId())))
                                .flatMapMany(actingProjectUser -> sprintRepository.findById(dto.getSprintId())
                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + dto.getSprintId())))
                                        .flatMapMany(sprint -> Flux.fromIterable(dto.getUserIds())
                                                .flatMap(userId ->
                                                        sprintUserRepository.findBySprintIdAndUserId(dto.getSprintId(), userId)
                                                                .flatMap(existing -> {
                                                                    // Kullanıcı zaten ekliyse atla (hata fırlatma)
                                                                    return Mono.<SprintUserDto>empty(); // Tipi açıkça belirtildi
                                                                })
                                                                .switchIfEmpty(Mono.defer(() ->
                                                                        projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), userId)
                                                                                .flatMap(userToAddAsProjectUser -> {
                                                                                    SprintUser sprintUser = SprintUser.builder()
                                                                                            .sprintId(dto.getSprintId())
                                                                                            .projectId(dto.getProjectId())
                                                                                            .user(new CreatedBy(userToAddAsProjectUser))
                                                                                            .createdProject(new CreatedProject(project))
                                                                                            .assignmentDate(Instant.now())
                                                                                            .roleInSprint(dto.getRoleInSprint() != null ? dto.getRoleInSprint() : "Member")
                                                                                            .statusInSprint(dto.getStatusInSprint() != null ? dto.getStatusInSprint() : "Active")
                                                                                            .createdAt(LocalDateTime.now())
                                                                                            .updatedAt(LocalDateTime.now())
                                                                                            .build();
                                                                                    return sprintUserRepository.save(sprintUser).map(SprintUserDto::new);
                                                                                })
                                                                                .onErrorResume(NoSuchElementException.class, e -> {
                                                                                    // Kullanıcı projede yoksa atla (hata fırlatma)
                                                                                    return Mono.<SprintUserDto>empty(); // Tipi açıkça belirtildi
                                                                                })
                                                                ))
                                                )
                                        )
                                )
                        )
                );
    }

    /**
     * Sprint'ten birden fazla proje kullanıcısını toplu olarak çıkartır.
     * Var olmayan kayıtları veya zaten sprintte olmayanları atlar.
     * @param dto Çıkartılacak kullanıcı ID'leri
     * @return Başarı durumunda boş Mono
     */
    public Mono<Void> removeBulkProjectUsersFromSprint(SprintUserBulkRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser -> projectRepository.findById(dto.getProjectId())
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found with ID: " + dto.getProjectId())))
                        .flatMap(project -> projectUserRepository.findByProjectIdAndUserId(project.getId(), currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("Current user is not a member of project " + dto.getProjectId())))
                                .flatMap(actingProjectUser -> sprintRepository.findById(dto.getSprintId())
                                        .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + dto.getSprintId())))
                                        .flatMapMany(sprint -> Flux.fromIterable(dto.getUserIds())
                                                .flatMap(userId -> sprintUserRepository.findBySprintIdAndUserId(dto.getSprintId(), userId)
                                                        .flatMap(sprintUserRepository::delete)
                                                        .switchIfEmpty(Mono.empty()) // Zaten ekli olmayanları atla
                                                )
                                        ).then()
                                )
                        )
                );
    }

    /**
     * Belirli bir sprint'teki tüm atanan proje kullanıcılarını listeler.
     * Kullanıcının ilgili projenin üyesi olması gerekmektedir.
     * @param sprintId Listelenecek sprint'in ID'si
     * @return Sprint'teki kullanıcıların DTO'ları (Flux olarak)
     */
    public Flux<SprintUserDto> getSprintUsers(String sprintId) {
        return authHelperService.getAuthUser()
                .flatMapMany(currentUser -> sprintRepository.findById(sprintId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("Sprint not found with ID: " + sprintId)))
                        .flatMapMany(sprint -> projectUserRepository.findByProjectIdAndUserId(sprint.getCreatedProject().getId(), currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("Current user is not a member of the project this sprint belongs to.")))
                                .flatMapMany(actingProjectUser -> sprintUserRepository.findBySprintId(sprintId)
                                        .map(SprintUserDto::new)
                                )
                        )
                );
    }

    /**
     * Belirli bir sprint'teki belirli bir proje kullanıcısını getirir.
     * Kullanıcının ilgili projenin üyesi olması gerekmektedir.
     * @param sprintId Kullanıcının bulunduğu sprint'in ID'si
     * @param userId Getirilecek kullanıcının ID'si
     * @return SprintUser'ın DTO'su
     */
    public Mono<SprintUserDto> getSprintUser(String sprintId, String userId) {
        return authHelperService.getAuthUser()
                .flatMap(currentUser -> sprintUserRepository.findBySprintIdAndUserId(sprintId, userId)
                        .switchIfEmpty(Mono.error(new NoSuchElementException("User with ID " + userId + " not found in sprint " + sprintId)))
                        .flatMap(sprintUser -> projectUserRepository.findByProjectIdAndUserId(sprintUser.getProjectId(), currentUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("Current user is not a member of the project this sprint user belongs to.")))
                                .map(actingProjectUser -> new SprintUserDto(sprintUser))
                        )
                );
    }
}