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
    @Override
    public Mono<Void> sendCustomInvitationEmail(String to, String projectName, String registrationLink) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setFrom(from);
                message.setSubject("You're invited to join project: " + projectName);
                message.setText("You've been invited to the project '" + projectName + "'.\nClick the link to register and accept: " + registrationLink);
                mailSender.send(message);
                System.out.println("[EMAIL SENT] Invitation email sent to: " + to);
            } catch (Exception e) {
                System.err.println("[EMAIL ERROR] Failed to send invitation email to: " + to);
                e.printStackTrace(); // Hatanın detaylarını konsola yazdırır
            }
        });
    }
    @Override
    public Mono<Void> sendCustomEmail(String to, String subject, String content) {
        return Mono.fromRunnable(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setFrom(from);
                message.setSubject(subject);
                message.setText(content);
                mailSender.send(message);
                System.out.println("[EMAIL SENT] Custom email sent to: " + to + " with subject: " + subject);
            } catch (Exception e) {
                System.err.println("[EMAIL ERROR] Failed to send custom email to: " + to);
                e.printStackTrace();
            }
        });
    }
}
