package com.tracker.job_ts.projectTask.controller;

import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.projectTask.dto.projectTask.ProjectTaskDto;
import com.tracker.job_ts.projectTask.dto.projectTask.ProjectTaskFltreRequestDto;
import com.tracker.job_ts.projectTask.dto.projectTask.ProjectTaskRequestDto;
import com.tracker.job_ts.projectTask.dto.projectTask.UpdateProjectTaskStatusRequestDto;
import com.tracker.job_ts.projectTask.service.ProjectTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.ProjectTaskCtrl.CTRL)
@RequiredArgsConstructor
public class ProjectTaskController {

    private final ProjectTaskService projectTaskService;

    /**
     * Yeni bir proje görevi oluşturur.
     * @param dto Oluşturulacak görevin bilgileri
     * @return Oluşturulan görevin DTO'su
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Görev başarıyla oluşturulduğunda 201 Created döner
    public Mono<ProjectTaskDto> create(@RequestBody ProjectTaskRequestDto dto) {
        return projectTaskService.createTask(dto);
    }
    /**
     * Belirli bir görev ID'sine göre görevi getirir.
     * @param taskId Getirilecek görevin ID'si
     * @return Görevin DTO'su
     */
    @GetMapping("/{taskId}")
    public Mono<ProjectTaskDto> getByTaskId(@PathVariable String taskId) {
        return projectTaskService.getByTaskId(taskId);
    }

    /**
     * Belirli bir görevi günceller.
     * @param taskId Güncellenecek görevin ID'si
     * @param dto Güncelleme bilgileri
     * @return Güncellenmiş görevin DTO'su
     */
    @PutMapping("/{taskId}")
    public Mono<ProjectTaskDto> update(@PathVariable String taskId,
                                       @RequestBody ProjectTaskRequestDto dto) {
        return projectTaskService.updateTask(taskId, dto);
    }
    /**
     * Proje görevlerini filtrelenmiş olarak getirir.
     * @param filterDto Filtreleme kriterleri
     * @param page Sayfa numarası (varsayılan 0)
     * @param size Sayfa boyutu (varsayılan 10)
     * @return Filtrelenmiş görevlerin sayfalanmış sonucu
     */
    @PostMapping("/filter")
    public Mono<PagedResult<ProjectTaskDto>> filterTasks(
            @RequestBody ProjectTaskFltreRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return projectTaskService.getAllFilteredTasks(filterDto, page, size);
    }
    /**
     * Belirli bir görevin parent task'ını günceller.
     * @param taskId Parent'ı güncellenecek görevin ID'si
     * @param newParentTaskId Yeni parent task'ın ID'si. Eğer null veya boş ise parent task kaldırılır.
     * @return Güncellenmiş görevin DTO'su
     */
    @PutMapping("/parent/{taskId}")
    public Mono<ProjectTaskDto> updateTaskParent(
            @PathVariable String taskId,
            @RequestBody ProjectTaskRequestDto dto) { // newParentTaskId null olabilir
        return projectTaskService.updateParentTask(taskId, dto);
    }

    /**
     * Belirli bir parent task ID'sine sahip tüm alt görevleri getirir.
     * @param taskId Alt görevleri getirilecek olan parent görevin ID'si
     * @return Belirtilen parent task'a ait alt görevlerin bir listesi
     */
    @GetMapping("/subtasks/{taskId}")
    public Flux<ProjectTaskDto> getSubtasks(@PathVariable String taskId) {
        return projectTaskService.getSubtasksByParentTaskId(taskId);
    }

    /**
     * Bir proje görevinin durumunu günceller.
     * @param dto Durumu güncellenecek görevin bilgileri
     * @return Güncellenmiş görevin DTO'su
     */
    @PostMapping("/update-status")
    public Mono<ProjectTaskDto> updateTaskStatus(@RequestBody UpdateProjectTaskStatusRequestDto dto) {
        return projectTaskService.updateTaskStatus(dto);
    }
}
