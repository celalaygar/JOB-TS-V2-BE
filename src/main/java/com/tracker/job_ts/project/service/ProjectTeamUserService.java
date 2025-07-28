package com.tracker.job_ts.project.service;


import com.tracker.job_ts.auth.exception.UserNotFoundException;
import com.tracker.job_ts.auth.repository.UserRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.entity.ProjectUser;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.exception.projectTeam.ProjectTeamValidationException;
import com.tracker.job_ts.project.repository.ProjectRepository;
import com.tracker.job_ts.project.repository.ProjectTeamRepository;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectTeamUserService {

    private final ProjectTeamRepository projectTeamRepository;
    private final ProjectRepository projectRepository;
    private final ProjectUserRepository projectUserRepository;
    private final UserRepository userRepository;
    private final AuthHelperService authHelperService;
    /**
     * Adds multiple users to a specific project team.
     * Before adding, it performs several checks for each user:
     * 1. Ensures the authenticated user is the creator of the project.
     * 2. Validates that the project and team exist.
     * 3. Checks if each target user exists.
     * 4. Verifies if each target user is already associated with the project.
     * 5. Prevents adding a user to the team if they are already a member of that specific team.
     *
     * @param teamId The ID of the project team to add the users to.
     * @param userIdsToAdd A list of IDs of the users to be added to the team.
     * @return A Flux emitting the updated/created ProjectUser objects for each successfully added user.
     */
    public Flux<ProjectUser> addUsersToTeam(String teamId, List<String> userIdsToAdd) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> projectTeamRepository.findById(teamId)
                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Proje Takımı bulunamadı: " + teamId)))
                        .flatMapMany(projectTeam -> projectRepository.findByIdAndCreatedByUserId(projectTeam.getCreatedProject().getId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Proje bulunamadı veya proje yaratıcısı siz değilsiniz.")))
                                .flatMapMany(project -> Flux.fromIterable(userIdsToAdd) // Her bir userId için işlem yap
                                        .flatMap(userId -> userRepository.findById(userId) // Kullanıcının sistemde varlığını kontrol et
                                                .switchIfEmpty(Mono.error(new UserNotFoundException("Eklenecek kullanıcı bulunamadı: " + userId)))
                                                .flatMap(userToAdd -> projectUserRepository.findByProjectIdAndUserId(project.getId(), userId)
                                                        .flatMap(existingProjectUser -> {
                                                            // Kullanıcı projede kayıtlı, şimdi takıma ekle
                                                            List<String> currentTeamIds = Optional.ofNullable(existingProjectUser.getProjectTeamIds())
                                                                    .orElseGet(ArrayList::new);

                                                            if (currentTeamIds.contains(teamId)) {
                                                                // Kullanıcı zaten bu takımda, hata döndürmek yerine sadece loglayıp geçebiliriz
                                                                // veya bu kullanıcıyı akıştan filtreleyebiliriz.
                                                                // Şimdilik hata fırlatıyoruz, ancak birden fazla kullanıcı eklerken bu akışı kesebilir.
                                                                // Eğer diğer kullanıcıların devam etmesini istiyorsanız Mono.empty() döndürebilirsiniz.
                                                                return Mono.error(new ProjectTeamValidationException("Kullanıcı " + userId + " zaten bu takımın bir üyesi."));
                                                            }

                                                            currentTeamIds.add(teamId);
                                                            existingProjectUser.setProjectTeamIds(currentTeamIds);
                                                            existingProjectUser.setTeamMember(!currentTeamIds.isEmpty());
                                                            existingProjectUser.setUpdatedAt(LocalDateTime.now());
                                                            return projectUserRepository.save(existingProjectUser);
                                                        })
                                                        .switchIfEmpty(
                                                                // Kullanıcı projede kayıtlı değil, hata fırlat
                                                                Mono.error(new ProjectTeamValidationException("Kullanıcı " + userId + " projede kayıtlı değil. Lütfen önce projeye kaydedin."))
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    /**
     * Bir kullanıcıyı belirli bir proje takımından çıkarır.
     * Kullanıcının projede ProjectUser kaydı yoksa veya ilgili takımda değilse işlem yapmaz.
     *
     * @param teamId Çıkarılacak proje takımının ID'si.
     * @param userIdToRemove Çıkarılacak kullanıcının ID'si.
     * @return İşlemin tamamlandığını belirten bir Mono.
     */
    public Mono<Void> removeUserFromTeam(String teamId, String userIdToRemove) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> projectTeamRepository.findById(teamId)
                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Project Team not found: " + teamId)))
                        .flatMap(projectTeam -> projectRepository.findByIdAndCreatedByUserId(projectTeam.getCreatedProject().getId(), authUser.getId())
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Proje bulunamadı veya proje yaratıcısı siz değilsiniz.")))
                                .flatMap(project -> projectUserRepository.findByProjectIdAndUserId(project.getId(), userIdToRemove)
                                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("User not found in Project"))) // **Burada yeni kontrol!**
                                        .flatMap(existingProjectUser -> {
                                            List<String> currentTeamIds = Optional.ofNullable(existingProjectUser.getProjectTeamIds())
                                                    .orElseGet(ArrayList::new);

                                            if (!currentTeamIds.contains(teamId)) {
                                                return Mono.error(new ProjectTeamValidationException("Kullanıcı bu takımın bir üyesi değil."));
                                            }

                                            currentTeamIds.remove(teamId);
                                            existingProjectUser.setProjectTeamIds(currentTeamIds);
                                            existingProjectUser.setTeamMember(!currentTeamIds.isEmpty()); // Hiçbir takıma ait değilse team üyesi değildir
                                            existingProjectUser.setUpdatedAt(LocalDateTime.now());
                                            return projectUserRepository.save(existingProjectUser).then();
                                        })
                                )
                        )
                );
    }

    /**
     * Belirli bir proje takımına ait tüm kullanıcıları getirir.
     * Authenticated kullanıcının ilgili projede ProjectUser kaydı olması yeterlidir.
     *
     * @param projectId İlgili projenin ID'si.
     * @param teamId İlgili proje takımının ID'si.
     * @return Takıma ait ProjectUser nesnelerinin bir Flux'ı.
     */
    public Flux<ProjectUser> getUsersByTeamId(String projectId, String teamId) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> projectUserRepository.findByProjectIdAndUserId(projectId, authUser.getId()) // Auth olmuş kullanıcının projede ProjectUser kaydı var mı kontrolü
                        .switchIfEmpty(Mono.error(new ProjectNotFoundException("Login in User not found in Project")))
                        .flatMapMany(authProjectUser -> projectRepository.findById(projectId) // Proje var mı kontrolü
                                .switchIfEmpty(Mono.error(new ProjectNotFoundException("Project not found: " + projectId)))
                                .flatMapMany(project -> projectTeamRepository.findByIdAndCreatedProjectId(teamId, projectId) // Takım var mı ve projeye ait mi kontrolü
                                        .switchIfEmpty(Mono.error(new ProjectTeamValidationException("Proje Takımı bulunamadı veya bu projeye ait değil: " + teamId)))
                                        // Yeni repository metodunu kullanarak doğrudan sorgulama
                                        .flatMapMany(projectTeam -> projectUserRepository.findByProjectIdAndProjectTeamIdsContaining(projectId, teamId))
                                )
                        )
                );
    }
}