package com.tracker.job_ts.project.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.project.dto.ProjectRolePermissionDto;
import com.tracker.job_ts.project.model.ProjectRolePermissionEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;

@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/permissions")
public class ProjectRolePermissionController {

    @GetMapping("/project-user-role")
    public Flux<ProjectRolePermissionDto> getAllPermissions() {
        return Flux.fromStream(Arrays.stream(ProjectRolePermissionEnum.values())
                .map(permission -> new ProjectRolePermissionDto(
                        permission.getId(),
                        permission.getLabelEn(),
                        permission.getLabelTr(),
                        permission.getLabelEn(),
                        permission.getCategory()
                )));
    }
}
