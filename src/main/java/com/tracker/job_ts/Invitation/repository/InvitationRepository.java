package com.tracker.job_ts.Invitation.repository;


import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.Invitation.entity.InvitationStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvitationRepository extends ReactiveMongoRepository<Invitation, String> {
    Flux<Invitation> findByInvitedUserEmailAndStatus(String email, InvitationStatus status);
    Mono<Invitation> findByIdAndInvitedUserEmail(String id, String email);
    Mono<Invitation> findByToken( String token);
    Flux<Invitation> findByProjectId(String projectId);
    Flux<Invitation> findByProjectIdAndStatusIsNot(String projectId, InvitationStatus status);
    Mono<Boolean> existsByProjectIdAndInvitedUserEmail(String projectId, String email);

    default Mono<Boolean> hasExistingInvitation(String projectId, String email) {
        return findByProjectIdAndInvitedUserEmail(projectId, email).hasElements();
    }
    Flux<Invitation> findByProjectIdAndInvitedUserEmailAndStatusIsNot(String projectId, String email, InvitationStatus status);
    Flux<Invitation> findByProjectIdAndInvitedUserEmail(String projectId, String email);

}