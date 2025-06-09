package com.tracker.job_ts.Invitation.service;

import com.tracker.job_ts.Invitation.dto.InvitationRequestDto;
import com.tracker.job_ts.Invitation.dto.InviteToProjectRequestDto;
import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.Invitation.entity.InvitationStatus;
import com.tracker.job_ts.Invitation.entity.ProjectSummary;
import com.tracker.job_ts.Invitation.mapper.UserSummaryMapper;
import com.tracker.job_ts.Invitation.repository.InvitationRepository;
import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.email.service.EmailService;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final AuthHelperService authHelperService;
    private final EmailService emailService;
    private final ProjectUserRepository projectUserRepository;
    private final JWTProvider jwtService;


    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public Flux<Invitation> getAllInvitationByProjectId(InvitationRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectUserRepository
                        .findByProjectIdAndUserId(dto.getProjectId(), authUser.getId())
                        .switchIfEmpty(Mono.error(new RuntimeException("Kullanıcı bu projede yer almıyor")))
                        .flatMap(projectUser -> {
                            if (projectUser.getProjectSystemRole() == ProjectSystemRole.PROJECT_ADMIN) {
                                return Mono.empty(); // Yetkiliyse akışı sürdür
                            } else {
                                return Mono.error(new RuntimeException("Bu işlem için yetkiniz yok. (Sadece PROJECT_ADMIN erişebilir)"));
                            }
                        })
                )
                .thenMany(invitationRepository.findByProjectId(dto.getProjectId()));
    }


    @Override
    public Mono<Object> inviteUserToProject(InviteToProjectRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectRepository.findById(dto.getProjectId())
                        .switchIfEmpty(Mono.error(new RuntimeException("Project not found")))
                        .flatMap(project -> userRepository.findByEmail(dto.getEmail())
                                .defaultIfEmpty(User.builder().email(dto.getEmail()).build())
                                .flatMap(invitedUser -> {
                                    // 1. Kullanıcı projede aktif bir üye mi?
                                    return projectUserRepository.findByProjectIdAndEmail(dto.getProjectId(), dto.getEmail())
                                            .flatMap(existingProjectUser -> Mono.error(new RuntimeException("Kullanıcı zaten bu projede yer alıyor.")))
                                            .switchIfEmpty(
                                                    // 2. Daha önce davet edilmiş mi?
                                                    invitationRepository.findByProjectIdAndInvitedUserEmailAndStatusIsNot(dto.getProjectId(), dto.getEmail(), InvitationStatus.DECLINED)
                                                            .hasElements()
                                                            .flatMap(alreadyInvited -> {
                                                                if (alreadyInvited) {
                                                                    return Mono.error(new RuntimeException("Bu kullanıcı zaten davet edilmiş."));
                                                                }

                                                                // Davet işlemi başlatılabilir
                                                                String token = jwtService.generateInvitationToken(dto.getEmail(), dto.getProjectId());
                                                                Invitation invitation = Invitation.builder()
                                                                        .status(InvitationStatus.PENDING)
                                                                        .invitedBy(UserSummaryMapper.mapUser(authUser))
                                                                        .invitedUser(UserSummaryMapper.mapUser(invitedUser))
                                                                        .project(ProjectSummary.builder()
                                                                                .id(project.getId())
                                                                                .name(project.getName())
                                                                                .build())
                                                                        .tokenExpiry(LocalDateTime.now().plusHours(1))
                                                                        .createdAt(LocalDateTime.now())
                                                                        .build();

                                                                if (invitedUser.getId() == null) {
                                                                    invitation.setToken(token);
                                                                    String registrationLink = frontendUrl + "register/invite/" + token;
                                                                    return emailService.sendCustomInvitationEmail(dto.getEmail(), project.getName(), registrationLink)
                                                                            .then(invitationRepository.save(invitation));
                                                                }

                                                                return invitationRepository.save(invitation);
                                                            })
                                            );
                                })
                        )
                );
    }


    @Override
    public Flux<Invitation> getPendingInvitationsForAuthUser() {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> invitationRepository.findByInvitedUserEmailAndStatus(
                        authUser.getEmail(), InvitationStatus.PENDING));
    }

    @Override
    public Mono<Invitation> acceptInvitation(InvitationRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> invitationRepository.findByIdAndInvitedUserEmail(dto.getInvitationId(), user.getEmail())
                        .switchIfEmpty(Mono.error(new RuntimeException("Invitation not found")))
                        .flatMap(invitation -> {
                            invitation.setStatus(InvitationStatus.ACCEPTED);

                            return projectRepository.findById(invitation.getProject().getId())
                                    .switchIfEmpty(Mono.error(new RuntimeException("Project not found")))
                                    .flatMap(project -> {
                                        String projectId = project.getId();
                                        String userId = user.getId();

                                        // ProjectUser daha önce eklenmiş mi kontrolü
                                        return projectUserRepository.findByProjectIdAndUserId(projectId, userId)
                                                .flatMap(existing -> Mono.empty()) // zaten varsa hiçbir şey yapma
                                                .switchIfEmpty(Mono.defer(() -> {
                                                    ProjectUser projectUser = ProjectUser.builder()
                                                            .projectId(projectId)
                                                            .userId(userId)
                                                            .email(user.getEmail())
                                                            .firstname(user.getFirstname())
                                                            .lastname(user.getLastname())
                                                            .isCreator(false)
                                                            .isTeamMember(true)
                                                            .projectSystemRole(ProjectSystemRole.PROJECT_USER) // veya başka bir default rol
                                                            .assignedBy(invitation.getInvitedBy().getId())
                                                            .assignedAt(LocalDateTime.now())
                                                            .build();
                                                    return projectUserRepository.save(projectUser);
                                                }))
                                                .then(invitationRepository.save(invitation));
                                    });
                        }));
    }


    @Override
    public Mono<Invitation> declineInvitation(InvitationRequestDto dto) {
        return authHelperService.getAuthUser()
                .flatMap(user -> invitationRepository.findByIdAndInvitedUserEmail(dto.getInvitationId(), user.getEmail())
                        .switchIfEmpty(Mono.error(new RuntimeException("Invitation not found")))
                        .flatMap(invitation -> {
                            invitation.setStatus(InvitationStatus.DECLINED);
                            return invitationRepository.save(invitation)
                                    .flatMap(saved -> {
                                        // Bildirim gönder
                                        String to = invitation.getInvitedBy().getEmail();
                                        return emailService.sendDeclineNotificationEmail(to, invitation.getProject().getName())
                                                .thenReturn(saved);
                                    });
                        }));
    }

}
