package com.tracker.job_ts.auth.service;


import com.tracker.job_ts.Invitation.entity.InvitationStatus;
import com.tracker.job_ts.Invitation.mapper.UserSummaryMapper;
import com.tracker.job_ts.Invitation.repository.InvitationRepository;
import com.tracker.job_ts.auth.config.JWTProvider;
import com.tracker.job_ts.auth.dto.AuthRequest;
import com.tracker.job_ts.auth.dto.AuthResponse;
import com.tracker.job_ts.auth.dto.RegisterRequest;
import com.tracker.job_ts.auth.entity.SystemRole;
import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.exception.UnauthorizedException;
import com.tracker.job_ts.auth.mapper.RegisterRequestToUserMapper;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTProvider jwtProvider;
    private final TokenLogService tokenLogService;
    private final RegisterRequestToUserMapper registerRequestToUserMapper;

    private final InvitationRepository invitationRepository;
    private final ProjectUserRepository projectUserRepository;


    public Mono<Object> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return Mono.error(new RuntimeException("Passwords do not match"));
        }
        return userRepository.findByEmail(request.getEmail())
                .flatMap(existing -> Mono.error(new RuntimeException("Email already exists")))
                .switchIfEmpty(Mono.defer(() -> {
                    User user = registerRequestToUserMapper.map(request);
                    user.setCreatedAt(LocalDateTime.now());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user)
                            .thenReturn("User registered successfully");
                }));
    }


    public Mono<AuthResponse> login(AuthRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid User Email or Password")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new UnauthorizedException("Invalid credentials."));
                    }

                    List<SystemRole> roles = user.getSystemRoles().stream().collect(Collectors.toList());
                    String token = jwtProvider.generateToken(user.getEmail(), roles);

                    return tokenLogService.logToken(user, token, Instant.now())
                            .thenReturn(new AuthResponse(user,token));
                });
    }

    public Mono<Object> registerViaInvitation(RegisterRequest request) {
        Claims claims;
        try {
            claims = jwtProvider.parseInvitationToken(request.getToken());
        } catch (RuntimeException e) {
            return Mono.error(e); // parseInvitationToken zaten spesifik hata fırlatıyor
        }

        String email = claims.getSubject();
        String projectId = claims.get("projectId", String.class);

        return userRepository.findByEmail(email)
                .flatMap(existing -> Mono.error(new RuntimeException("Bu email ile kayıtlı bir kullanıcı zaten var.")))
                .switchIfEmpty(Mono.defer(() -> {
                    User newUser = registerRequestToUserMapper.map(request, email);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());

                    return userRepository.save(newUser)
                            .flatMap(savedUser ->
                                    invitationRepository.findByTokenAndStatus(request.getToken(), InvitationStatus.PENDING)
                                            .flatMap(invitation -> {
                                                invitation.setStatus(InvitationStatus.ACCEPTED);
                                                invitation.setInvitedUser(UserSummaryMapper.mapUser(savedUser));

                                                ProjectUser pu = ProjectUser.builder()
                                                        .userId(savedUser.getId())
                                                        .email(savedUser.getEmail())
                                                        .firstname(savedUser.getFirstname())
                                                        .lastname(savedUser.getLastname())
                                                        .projectId(projectId)
                                                        .isProjectMember(true)
                                                        .projectRole(invitation.getProjectRole())
                                                        .projectSystemRole(ProjectSystemRole.PROJECT_USER)
                                                        .assignedBy(invitation.getInvitedBy().getId())
                                                        .assignedAt(LocalDateTime.now())
                                                        .createdAt(LocalDateTime.now())
                                                        .isCreator(false)
                                                        .build();

                                                return projectUserRepository.save(pu)
                                                        .then(invitationRepository.save(invitation))
                                                        .thenReturn("Kayıt başarılı ve projeye eklendi.");
                                            }));
                }));
    }


}
