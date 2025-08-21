package com.tracker.job_ts.email.service;

import reactor.core.publisher.Mono;

public interface EmailService {
    Mono<Void> sendInvitationEmail(String to, String projectName);
    Mono<Void> sendInvitationEmail(String email, String teamName, String projectName);
    Mono<Void> sendDeclineNotificationEmail(String to, String projectName);
    Mono<Void> sendCustomInvitationEmail(String to, String projectName, String registrationLink);
    Mono<Void> sendCustomEmail(String to, String subject, String content);


}