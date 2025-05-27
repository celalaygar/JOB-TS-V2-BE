package com.tracker.job_ts.Invitation.service;

import com.tracker.job_ts.Invitation.entity.Invitation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvitationService {
    Mono<Invitation> inviteUserToProject(String projectId, String email);
    Flux<Invitation> getPendingInvitationsForAuthUser();
    Mono<Invitation> acceptInvitation(String invitationId);
    Mono<Invitation> declineInvitation(String invitationId);
}