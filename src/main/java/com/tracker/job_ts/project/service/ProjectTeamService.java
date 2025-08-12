package com.tracker.job_ts.project.service;

import com.tracker.job_ts.Invitation.entity.*;
import com.tracker.job_ts.Invitation.repository.InvitationRepository;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.email.service.EmailService;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamResponseDTO;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamDto;
import com.tracker.job_ts.project.dto.projectTeam.ProjectTeamInviteUserRequestDto;
import com.tracker.job_ts.project.entity.ProjectTeam;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.exception.projectTeam.ProjectTeamValidationException;
import com.tracker.job_ts.project.mapper.ProjectTeamMapper;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTeamRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectTeamService {

    private final ProjectTeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final AuthHelperService authHelperService;
    private final ProjectTeamMapper mapper;
    private final ProjectTeamValidationService validationService;
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    public Mono<ProjectTeamResponseDTO> create(ProjectTeamDto dto) {
        return validationService.validate(dto)
                .then(authHelperService.getAuthUser())
                .flatMap(user -> projectUserRepository.findByProjectIdAndUserId(dto.getProjectId(), user.getId())
                        .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                        .flatMap(projectUser -> projectRepository.findById(dto.getProjectId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found: " + dto.getProjectId())))
                                .flatMap(project -> teamRepository.existsByCreatedProjectIdAndName(dto.getProjectId(), dto.getName())
                                        .flatMap(exists -> {
                                            if (exists) {
                                                return Mono.error(new ProjectTeamValidationException("Team name already exists"));
                                            }
                                            ProjectTeam entity = mapper.toEntity(dto, project, user);
                                            entity.setCreatedAt(LocalDateTime.now());
                                            entity.setUpdatedAt(LocalDateTime.now());
                                            return teamRepository.save(entity);
                                        })
                                )
                        )
                )
                .map(mapper::toDto);
    }

    public Mono<ProjectTeamResponseDTO> update(ProjectTeamDto dto) {
        return validationService.validate(dto)
                .then(authHelperService.getAuthUser())
                .flatMap(user -> teamRepository.findById(dto.getId())
                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Team not found")))
                        .flatMap(existingTeam -> projectUserRepository.findByProjectIdAndUserId(existingTeam.getCreatedProject().getId(), user.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                                .flatMap(projectUser -> projectRepository.findById(dto.getProjectId())
                                        .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found")))
                                        .flatMap(project -> {

                                            if (!project.getId().equals(existingTeam.getCreatedProject().getId())) {
                                                return Mono.error(new ProjectTeamValidationException("Project Not found for teams"));
                                            }
                                            ProjectTeam updated = mapper.toUpdatedEntity(existingTeam, dto, project);
                                            return teamRepository.save(updated);
                                        })
                                )
                        )
                ).map(mapper::toDto);
    }

    public Mono<ProjectTeamResponseDTO> getById(ProjectTeamDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> projectUserRepository.findByProjectIdAndUserIdAndProjectSystemRoleNot(dto.getProjectId(), user.getId(), ProjectSystemRole.PROJECT_REMOVED_USER)
                        .switchIfEmpty(Mono.error(new IllegalAccessException("User is not a member of this project.")))
                        .flatMap(projectUser -> teamRepository.findByIdAndCreatedProjectId(dto.getId(), dto.getProjectId())
                                .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Team not found")))
                                .map(mapper::toDto)
                        )
                );
    }

    public Flux<ProjectTeamResponseDTO> getByProjectId(String projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found")))
                .flatMapMany(project -> teamRepository.findByCreatedProjectId(projectId))
                .map(mapper::toDto);
    }

    public Mono<Void> delete(String id) {
        return teamRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Team not found")))
                .flatMap(team -> teamRepository.deleteById(id));
    }

    public Mono<Invitation> inviteUserToTeam(ProjectTeamInviteUserRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> teamRepository.findById(dto.getTeamId())
                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Team not found")))
                        .flatMap(team -> {
                            String projectId = team.getCreatedProject().getId();
                            return projectRepository.findById(projectId)
                                    .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found")))
                                    .flatMap(project -> userRepository.findByEmail(dto.getEmail())
                                            .defaultIfEmpty(User.builder().email(dto.getEmail()).build()) // User not registered
                                            .flatMap(invitedUser -> {
                                                Invitation invitation = Invitation.builder()
                                                        .status(InvitationStatus.PENDING)
                                                        .invitedBy(mapUser(authUser))
                                                        .invitedUser(mapUser(invitedUser))
                                                        .team(ProjectTeamSummary.builder()
                                                                .id(team.getId())
                                                                .name(team.getName())
                                                                .build())
                                                        .project(ProjectSummary.builder()
                                                                .id(project.getId())
                                                                .name(project.getName())
                                                                .build())
                                                        .createdAt(LocalDateTime.now())
                                                        .build();

                                                if (invitedUser.getId() == null) {
                                                    // Guest user - send email
                                                    return emailService.sendInvitationEmail(dto.getEmail(), team.getName(), project.getName())
                                                            .then(invitationRepository.save(invitation));
                                                }

                                                return invitationRepository.save(invitation);
                                            }));
                        }));
    }


    /**
     * Oturum açmış kullanıcının üye olduğu tüm projelerdeki takımları listeler.
     *
     * @return Kullanıcının dahil olduğu projelerdeki tüm takımların listesi
     */
    public Flux<ProjectTeamResponseDTO> getAll() {
        return authHelperService.getAuthUser()
                .flatMapMany(currentUser ->
                        projectUserRepository.findAllByUserId(currentUser.getId())
                                .map(ProjectUser::getProjectId)
                                .distinct()
                                .collectList()
                                .flatMapMany(projectIds ->
                                        // ProjectTeamRepository'de bu metotun olması gerekir: Flux<ProjectTeam> findByCreatedProjectIdIn(List<String> projectIds);
                                        teamRepository.findByCreatedProjectIdIn(projectIds)
                                )
                )
                .map(mapper::toDto);
    }


    private UserSummary mapUser(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}
