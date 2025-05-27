package com.tracker.job_ts.Invitation.repository;


import com.tracker.job_ts.Invitation.entity.Invitation;
import com.tracker.job_ts.Invitation.entity.InvitationStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvitationRepository extends ReactiveMongoRepository<Invitation, String> {
    Flux<Invitation> findByInvitedUserEmailAndStatus(String email, InvitationStatus status);
    Mono<Invitation> findByIdAndInvitedUserEmail(String id, String email);
}