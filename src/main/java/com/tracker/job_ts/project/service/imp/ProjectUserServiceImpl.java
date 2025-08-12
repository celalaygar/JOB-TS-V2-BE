package com.tracker.job_ts.project.service.imp;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.dto.ProjectDto;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import com.tracker.job_ts.project.mapper.ProjectUserMapper;
import com.tracker.job_ts.project.model.ProjectSystemRole;
import com.tracker.job_ts.project.model.ProjectSystemStatus;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.project.service.ProjectUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class ProjectUserServiceImpl implements ProjectUserService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;
    private final AuthHelperService authHelperService;


    @Override
    public Flux<ProjectUserResponseDto> listProjectUsers(String projectId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectUserRepository.findByProjectIdAndUserId(projectId, authUser.getId())
                                .switchIfEmpty(Mono.error(new AccessDeniedException("No access to this project.")))
                )
                .thenMany(projectUserRepository.findByProjectId(projectId))
                .flatMap(projectUser ->
                        userRepository.findById(projectUser.getUserId())
                                .map(user -> {
                                    user.setProjectSystemRole(projectUser.getProjectSystemRole());
                                    return ProjectUserMapper.toDto(projectUser);
                                })
                );
    }
    /**
     * Projeden bir kullanıcıyı çıkarır. Bu işlem, kullanıcının rolünü "PROJECT_REMOVED_USER" olarak günceller.
     * @param projectId Proje ID'si
     * @param userId Çıkarılacak kullanıcının ID'si
     * @return İşlemin başarılı olup olmadığını belirten bir Mono<Boolean>
     */
    public Mono<Boolean> removeUserFromProject(String projectId, String userId) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectUserRepository.findByProjectIdAndUserId(projectId, authUser.getId())
                        .switchIfEmpty(Mono.error(new AccessDeniedException("You do not have access to this project.")))
                        .flatMap(requestingProjectUser -> {
                            // Yetki kontrolü: Sadece PROJECT_ADMIN veya PROJECT_OWNER rolündeki kullanıcılar çıkarabilir.
                            if (requestingProjectUser.getProjectSystemRole() != ProjectSystemRole.PROJECT_ADMIN ) {
                                return Mono.error(new AccessDeniedException("You do not have permission to remove users from this project."));
                            }

                            // Çıkarılmak istenen kullanıcıyı bul
                            return projectUserRepository.findByProjectIdAndUserId(projectId, userId)
                                    .switchIfEmpty(Mono.error(new NoSuchElementException("User not found in the project.")))
                                    .flatMap(userToRemove -> {
                                        // Proje yöneticisi kendini çıkaramaz.
                                        if (userToRemove.getUserId().equals(authUser.getId())) {
                                            return Mono.error(new IllegalArgumentException("You cannot remove yourself."));
                                        }

                                        // Projenin yaratıcısı çıkarılamaz.
                                        if (userToRemove.getIsCreator() != null && userToRemove.getIsCreator()) {
                                            return Mono.error(new IllegalArgumentException("The project creator cannot be removed."));
                                        }

                                        // Kullanıcının rolünü ve proje üyeliği durumunu güncelle
                                        userToRemove.setProjectSystemRole(ProjectSystemRole.PROJECT_REMOVED_USER);
                                        userToRemove.setIsProjectMember(false);
                                        userToRemove.setUpdatedAt(LocalDateTime.now());
                                        userToRemove.setAssignedBy(authUser.getId());

                                        // Güncellenmiş nesneyi kaydet
                                        return projectUserRepository.save(userToRemove)
                                                .thenReturn(true);
                                    });
                        })
                );
    }

}