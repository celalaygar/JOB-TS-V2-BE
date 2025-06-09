package com.tracker.job_ts.projectTask.controller;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.projectTask.dto.TaskStatusOptionDTO;
import com.tracker.job_ts.projectTask.service.TaskStatusOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@RestController
@RequestMapping(ApiPaths.BASE_PATH_V2 + "/task-status-option")
@RequiredArgsConstructor
public class TaskStatusOptionController {

    private final TaskStatusOptionService service;

    @PostMapping
    public Mono<ResponseEntity<TaskStatusOptionDTO>> create(@RequestBody TaskStatusOptionDTO dto) {
        return service.create(dto).map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskStatusOptionDTO>> update(@PathVariable String id, @RequestBody TaskStatusOptionDTO dto) {
        return service.update(id, dto).map(ResponseEntity::ok);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskStatusOptionDTO>> getById(@PathVariable String id) {
        return service.getById(id).map(ResponseEntity::ok);
    }

    @GetMapping("/project-task-status/{projectTaskStatusId}")
    public Flux<TaskStatusOptionDTO> getByProjectTaskStatusId(@PathVariable String projectTaskStatusId) {
        return service.getByProjectTaskStatusId(projectTaskStatusId);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id) {
        return service.delete(id);
    }
}
