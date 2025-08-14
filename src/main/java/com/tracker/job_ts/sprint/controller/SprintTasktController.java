package com.tracker.job_ts.sprint.controller;

import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.sprint.dto.SprintDto;
import com.tracker.job_ts.sprint.dto.SprintRegisterDto;
import com.tracker.job_ts.sprint.dto.SprintTaskRequestDto;
import com.tracker.job_ts.sprint.entity.Sprint;
import com.tracker.job_ts.sprint.service.SprintService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import org.springframework.http.HttpStatus;


@RestController
@RequestMapping(ApiPaths.SprintTaskCtrl.CTRL) // "/api/v1/sprint-tasks"
@RequiredArgsConstructor
public class SprintTasktController { // Adı SprintTaskController olarak düzeltildi

    private final SprintService sprintService;

    /**
     * Belirli bir sprint ve projeye ait tüm görevleri getirir.
     * Kullanıcının ilgili projenin ve sprint'in üyesi olması gerekmektedir.
     * GET /api/v1/sprint-tasks/get-tasks?sprintId={sprintId}&projectId={projectId}
     *
     * @param sprintId  Görevleri getirilecek sprint'in ID'si
     * @param projectId Görevlerin ait olduğu projenin ID'si
     * @return Sprint'e atanmış görevlerin DTO'ları (Flux olarak)
     */
    @PostMapping("/get-all")
    public Flux<ProjectTaskDto> getTasksInSprint(
            @Valid @RequestBody SprintTaskRequestDto requestDto ) {
        // DTO'yu burada oluşturup service'e gönderiyoruz
        return sprintService.getProjectTasksBySprintAndProject(requestDto);
    }

    /**
     * Belirli bir görevi sprint'e atar.
     * Kullanıcının ilgili projenin ve sprint'in üyesi olması ve görevin projeye ait olması gerekmektedir.
     * POST /api/v1/sprint-tasks/add-task
     *
     * @param dto SprintTaskRequestDto, sprintId, projectId ve taskId içerir.
     * @return Güncellenmiş ProjectTaskDto
     */
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK) // 200 OK veya 201 CREATED uygun olabilir
    public Mono<ProjectTaskDto> addTaskToSprint(@Valid @RequestBody SprintTaskRequestDto dto) {
        // sprintId, projectId ve taskId zaten DTO içinde olduğundan doğrudan service'e iletiyoruz
        return sprintService.addTaskToSprint(dto);
    }

    /**
     * Belirli bir görevi sprint'ten çıkartır.
     * Kullanıcının ilgili projenin ve sprint'in üyesi olması ve görevin projeye ait olması gerekmektedir.
     * POST /api/v1/sprint-tasks/remove-task
     *
     * @param dto SprintTaskRequestDto, sprintId, projectId ve taskId içerir.
     * @return Güncellenmiş ProjectTaskDto
     */
    @PostMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ProjectTaskDto> removeTaskFromSprint(@Valid @RequestBody SprintTaskRequestDto dto) {
        // sprintId, projectId ve taskId zaten DTO içinde olduğundan doğrudan service'e iletiyoruz
        return sprintService.removeTaskFromSprint(dto);
    }
}