package com.tracker.job_ts.sprint.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.sprint.dto.SprintDto;
import com.tracker.job_ts.sprint.dto.SprintRegisterDto;
import com.tracker.job_ts.sprint.dto.SprintStatusUpdateRequestDto;
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
    @GetMapping("/{sprintId}")
    public Mono<ResponseEntity<SprintDto>> getById(@PathVariable String sprintId) {
        return sprintService.getById(sprintId)
                .map(ResponseEntity::ok);
    }
    @GetMapping("/project/{projectId}")
    public Flux<Sprint> getSprintsByProject(@PathVariable String projectId) {
        return sprintService.getAllByProject(projectId);
    }

    @GetMapping("/getAll")
    public Flux<SprintDto> getAll() {
        return sprintService.getAll();
    }

    @GetMapping("/non-completed/project/{projectId}")
    public Flux<SprintDto> getNonCompletedSprintsByProject(@PathVariable String projectId) {
        return sprintService.getNonCompletedSprintsByProjectId(projectId);
    }

    @PostMapping("/{sprintId}/users")
    public Mono<ResponseEntity<Void>> addUsersToSprint(
            @PathVariable String sprintId,
            @RequestParam String projectId,
            @RequestBody List<String> userIds ) {
        return sprintService.addUsersToSprint(sprintId, projectId, userIds)
                .thenReturn(ResponseEntity.ok().build());
    }
    /**
     * Bir sprint'in durumunu günceller.
     * sprintId ve yeni durum bilgisini DTO içinde POST isteği ile alır.
     *
     * @param dto Sprint ID ve yeni durum bilgisini içeren DTO
     * @return Güncellenmiş Sprint nesnesi
     */
    @PostMapping("/status")
    public Mono<ResponseEntity<SprintDto>> updateSprintStatus(@RequestBody SprintStatusUpdateRequestDto dto) {
        return sprintService.updateSprintStatus(dto)
                .map(ResponseEntity::ok);
    }
}
