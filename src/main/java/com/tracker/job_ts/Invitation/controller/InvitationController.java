package com.tracker.job_ts.Invitation.controller;

import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.Invitation.service.InvitationService;
import com.tracker.job_ts.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/{projectId}")
    public Mono<Invitation> invite(@PathVariable String projectId, @RequestParam String email) {
        return invitationService.inviteUserToProject(projectId, email);
    }

    @GetMapping("/pending")
    public Flux<Invitation> getPending() {
        return invitationService.getPendingInvitationsForAuthUser();
    }

    @PostMapping("/{invitationId}/accept")
    public Mono<Invitation> accept(@PathVariable String invitationId) {
        return invitationService.acceptInvitation(invitationId);
    }

    @PostMapping("/{invitationId}/decline")
    public Mono<Invitation> decline(@PathVariable String invitationId) {
        return invitationService.declineInvitation(invitationId);
    }
}
