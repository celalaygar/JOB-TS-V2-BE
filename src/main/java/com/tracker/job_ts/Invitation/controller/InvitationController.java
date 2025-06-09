package com.tracker.job_ts.Invitation.controller;

import com.tracker.job_ts.Invitation.dto.InvitationRequestDto;
import com.tracker.job_ts.Invitation.dto.InviteToProjectRequestDto;
import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.Invitation.service.InvitationService;
import com.tracker.job_ts.base.util.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/invitations/project")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/inviteToProject")
    public Mono<Object> invite(@RequestBody InviteToProjectRequestDto dto) {
        return invitationService.inviteUserToProject(dto);
    }

    @GetMapping("/pending")
    public Flux<Invitation> getPending() {
        return invitationService.getPendingInvitationsForAuthUser();
    }

    @PostMapping("/accept")
    public Mono<Invitation> accept(@RequestBody InvitationRequestDto dto) {
        return invitationService.acceptInvitation(dto);
    }

    @PostMapping("/decline")
    public Mono<Invitation> decline(@RequestBody InvitationRequestDto dto) {
        return invitationService.declineInvitation(dto);
    }

    // ✅ Yeni eklenen endpoint: Proje ID'ye göre davetleri getir (sadece PROJECT_ADMIN erişebilir)
    @PostMapping("/all-by-projectId")
    public Flux<Invitation> getAllByProjectId(@RequestBody InvitationRequestDto dto) {
        return invitationService.getAllInvitationByProjectId(dto);
    }
}
