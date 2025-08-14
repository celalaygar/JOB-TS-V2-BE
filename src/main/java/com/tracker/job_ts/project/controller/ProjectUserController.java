package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.auth.entity.User;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.ProjectUserDTO;
import com.tracker.job_ts.project.dto.projectUser.ProjectUserResponseDto;
import com.tracker.job_ts.project.dto.projectUser.RemoveProjectUserRequestDto;
import com.tracker.job_ts.project.service.ProjectService;
import com.tracker.job_ts.project.service.ProjectUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.tracker.job_ts.project.dto.ProjectDto;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/project-users")
@RequiredArgsConstructor
public class ProjectUserController {

    private final ProjectUserService projectUserService;

    // belirli bir projedeki removed olmayan tüm kullanıcıları getirir.
    @GetMapping("/get-active-users/project/{projectId}")
    public Flux<ProjectUserResponseDto> getProjectUsers(@PathVariable String projectId) {
        return projectUserService.listProjectUsers(projectId);
    }

    // belirli bir projedeki tüm kullanıcıları getirir.
    @GetMapping("/all-users/project/{projectId}")
    public Flux<ProjectUserResponseDto> getAllProjectUsers(@PathVariable String projectId) {
        return projectUserService.listAllProjectUsers(projectId);
    }
    /**
     * Projeden bir kullanıcıyı çıkarır.
     * @param dto Çıkarılacak kullanıcının proje ve kullanıcı ID'lerini içeren DTO
     * @return İşlemin başarılı olup olmadığını belirten bir Mono<Boolean>
     */
    @PostMapping("/remove-user")
    public Mono<Boolean> removeUserFromProject(@RequestBody RemoveProjectUserRequestDto dto) {
        return projectUserService.removeUserFromProject(dto.getProjectId(), dto.getUserId());
    }
}
