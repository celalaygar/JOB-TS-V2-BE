package com.tracker.job_ts.Invitation.service;

import com.tracker.job_ts.Invitation.dto.InvitationRequestDto;
import com.tracker.job_ts.Invitation.dto.InviteToProjectRequestDto;
import com.tracker.job_ts.Invitation.entity.Invitation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvitationService {
    Mono<Object> inviteUserToProject(InviteToProjectRequestDto dto);
    Flux<Invitation> getPendingInvitationsForAuthUser();
    Mono<Invitation> acceptInvitation(InvitationRequestDto dto);
    Mono<Invitation> declineInvitation(InvitationRequestDto dto);

    Flux<Invitation> getAllInvitationByProjectId(InvitationRequestDto dto);
}