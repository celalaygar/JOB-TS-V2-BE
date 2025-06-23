package com.tracker.job_ts.sprint.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.sprint.dto.SprintRegisterDto;
import com.tracker.job_ts.sprint.entity.Sprint;
import com.tracker.job_ts.sprint.service.SprintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.SprintCtrl.CTRL)
@RequiredArgsConstructor
public class SprintController {

    private final SprintService sprintService;

    @PostMapping
    public Mono<ResponseEntity<Sprint>> createSprint(@RequestBody SprintRegisterDto dto) {
        return sprintService.createSprint(dto)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Sprint>> updateSprint(@PathVariable String id, @RequestBody SprintRegisterDto dto) {
        return sprintService.updateSprint(id, dto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteSprint(@PathVariable String id) {
        return sprintService.deleteSprint(id)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/project/{projectId}")
    public Flux<Sprint> getSprintsByProject(@PathVariable String projectId) {
        return sprintService.getAllByProject(projectId);
    }

    @PostMapping("/{sprintId}/users")
    public Mono<ResponseEntity<Void>> addUsersToSprint(
            @PathVariable String sprintId,
            @RequestParam String projectId,
            @RequestBody List<String> userIds ) {
        return sprintService.addUsersToSprint(sprintId, projectId, userIds)
                .thenReturn(ResponseEntity.ok().build());
    }

    @DeleteMapping("/{sprintId}/users/{userId}")
    public Mono<ResponseEntity<Void>> removeUserFromSprint(
            @PathVariable String sprintId,
            @PathVariable String userId ) {
        return sprintService.removeUserFromSprint(sprintId, userId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
