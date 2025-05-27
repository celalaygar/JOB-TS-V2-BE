package com.tracker.job_ts.project.service;

import com.tracker.job_ts.Invitation.entity.*;
import com.tracker.job_ts.Invitation.repository.InvitationRepository;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.email.service.EmailService;
import com.tracker.job_ts.project.dto.teams.ProjectTeamResponseDTO;
import com.tracker.job_ts.project.dto.teams.ProjectTeamsDto;
import com.tracker.job_ts.project.dto.teams.ProjectTeamsInviteUserRequestDto;
import com.tracker.job_ts.project.entity.ProjectTeams;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.exception.projectTeam.ProjectTeamsValidationException;
import com.tracker.job_ts.project.mapper.ProjectTeamsMapper;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTeamRepository;
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
    private final ProjectTeamsMapper mapper;
    private final ProjectTeamsValidationService validationService;
    private final InvitationRepository invitationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public Mono<ProjectTeamResponseDTO> create(ProjectTeamsDto dto) {
        return validationService.validate(dto)
                .then(authHelperService.getAuthUser())
                .flatMap(user -> projectRepository.findByIdAndCreatedByUserId(dto.getProjectId(), user.getId())
                        .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found: " + dto.getProjectId())))
                        .flatMap(project -> teamRepository.existsByCreatedProjectIdAndName(dto.getProjectId(), dto.getName())
                                .flatMap(exists -> {
                                    if (exists) {
                                        return Mono.error(new ProjectTeamsValidationException("Team name already exists"));
                                    }
                                    ProjectTeams entity = mapper.toEntity(dto, project, user);
                                    entity.setCreatedAt(LocalDateTime.now());
                                    entity.setUpdatedAt(LocalDateTime.now());
                                    return teamRepository.save(entity);
                                })
                        )
                )
                .map(mapper::toDto);
    }

    public Mono<ProjectTeamResponseDTO> update( ProjectTeamsDto dto) {
        return validationService.validate(dto)
                .then(teamRepository.findById(dto.getId())
                        .switchIfEmpty(Mono.error(new ProjectTeamsValidationException("Team not found")))
                        .flatMap(existing -> projectRepository.findById(dto.getProjectId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found")))
                                .flatMap(project -> {
                                    ProjectTeams updated = mapper.toUpdatedEntity(existing, dto, project);
                                    return teamRepository.save(updated);
                                })
                        )
                )
                .map(mapper::toDto);
    }

    public Mono<ProjectTeamResponseDTO> getById(ProjectTeamsDto dto) {
        return  projectRepository.findById(dto.getProjectId())
                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found: " + dto.getProjectId())))
                .flatMap(project ->teamRepository.findByIdAndCreatedProjectId(dto.getId(),dto.getProjectId())
                        .switchIfEmpty(Mono.error(new ProjectTeamsValidationException("Team not found")))
                        .map(mapper::toDto));
    }

    public Flux<ProjectTeamResponseDTO> getByProjectId(String projectId) {
        return projectRepository.findById(projectId)
                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found")))
                .flatMapMany(project -> teamRepository.findByCreatedProjectId(projectId))
                .map(mapper::toDto);
    }

    public Mono<Void> delete(String id) {
        return teamRepository.findById(id)
                .switchIfEmpty(Mono.error(new ProjectTeamsValidationException("Team not found")))
                .flatMap(team -> teamRepository.deleteById(id));
    }

    public Mono<Invitation> inviteUserToTeam(ProjectTeamsInviteUserRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> teamRepository.findById(dto.getTeamId())
                        .switchIfEmpty(Mono.error(new ProjectTeamsValidationException("Team not found")))
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
                                                        .team(TeamSummary.builder()
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

    private UserSummary mapUser(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}
