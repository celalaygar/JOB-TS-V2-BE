package com.tracker.job_ts.project.mapper;

import com.tracker.job_ts.project.model.PermissionValue;
import com.tracker.job_ts.project.model.ProjectRolePermissionEnum;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionMapperService {

    public Mono<List<PermissionValue>> mapPermissionsToValues(List<String> permissionIds) {
        return Flux.fromArray(ProjectRolePermissionEnum.values())
                .map(enumVal -> {
                    boolean matched = permissionIds.contains(enumVal.getId());
                    return new PermissionValue(enumVal, enumVal.getId(), matched);
                })
                .collectList();
    }
}
