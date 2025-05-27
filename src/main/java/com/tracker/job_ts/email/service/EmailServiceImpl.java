package com.tracker.job_ts.email.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public Mono<Void> sendInvitationEmail(String to, String projectName) {
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(from);
            message.setSubject("Project Invitation");
            message.setText("You've been invited to join project: " + projectName + ".\nPlease register or login to accept.");
            mailSender.send(message);
        });
    }
    public Mono<Void> sendInvitationEmail(String email, String teamName, String projectName) {
        String subject = "You're invited to join a team!";
        String content = String.format("You've been invited to join the team '%s' in project '%s'.", teamName, projectName);
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content);
            mailSender.send(message);
        });
    }
    @Override
    public Mono<Void> sendDeclineNotificationEmail(String to, String projectName) {
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(from);
            message.setSubject("Invitation Declined");
            message.setText("The invitation for project '" + projectName + "' has been declined.");
            mailSender.send(message);
        });
    }
}
