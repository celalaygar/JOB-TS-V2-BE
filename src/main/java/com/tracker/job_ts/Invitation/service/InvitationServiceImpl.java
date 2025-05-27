package com.tracker.job_ts.Invitation.service;

import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.Invitation.entity.InvitationStatus;
import com.tracker.job_ts.Invitation.entity.ProjectSummary;
import com.tracker.job_ts.Invitation.entity.UserSummary;
import com.tracker.job_ts.Invitation.repository.InvitationRepository;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.email.service.EmailService;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.ProjectSystemUserRole;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
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

    @Override
    public Mono<Invitation> inviteUserToProject(String projectId, String email) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectRepository.findById(projectId)
                        .switchIfEmpty(Mono.error(new RuntimeException("Project not found")))
                        .flatMap(project -> userRepository.findByEmail(email)
                                .defaultIfEmpty(User.builder().email(email).build()) // Not registered user
                                .flatMap(invitedUser -> {
                                    Invitation invitation = Invitation.builder()
                                            .status(InvitationStatus.PENDING)
                                            .invitedBy(mapUser(authUser))
                                            .invitedUser(mapUser(invitedUser))
                                            .project(ProjectSummary.builder()
                                                    .id(project.getId())
                                                    .name(project.getName())
                                                    .build())
                                            .createdAt(LocalDateTime.now())
                                            .build();

                                    if (invitedUser.getId() == null) {
                                        return emailService.sendInvitationEmail(email, project.getName())
                                                .then(invitationRepository.save(invitation));
                                    }

                                    return invitationRepository.save(invitation);
                                })));
    }

    @Override
    public Flux<Invitation> getPendingInvitationsForAuthUser() {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> invitationRepository.findByInvitedUserEmailAndStatus(authUser.getEmail(), InvitationStatus.PENDING));
    }

    @Override
    public Mono<Invitation> acceptInvitation(String invitationId) {
        return authHelperService.getAuthUser()
                .flatMap(user -> invitationRepository.findByIdAndInvitedUserEmail(invitationId, user.getEmail())
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
                                                            .projectSystemUserRole(ProjectSystemUserRole.PROJECT_ADMIN) // veya başka bir default rol
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
    public Mono<Invitation> declineInvitation(String invitationId) {
        return authHelperService.getAuthUser()
                .flatMap(user -> invitationRepository.findByIdAndInvitedUserEmail(invitationId, user.getEmail())
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

    private UserSummary mapUser(User user) {
        return UserSummary.builder().build().builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .build();
    }
}
