package com.tracker.job_ts.kanban.controller;


import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.base.util.ApiPaths;
import com.tracker.job_ts.kanban.dto.KanbanTaskFilterRequestDto;
import com.tracker.job_ts.kanban.service.KanbanService;
import com.tracker.job_ts.projectTask.dto.projectTask.ProjectTaskDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(ApiPaths.KanbanCtrl.CTRL)
@RequiredArgsConstructor
public class KanbanController {

    private final KanbanService kanbanService;

    @PostMapping("/filter")
    public Mono<PagedResult<ProjectTaskDto>> getBacklogTasks(
            @Valid @RequestBody KanbanTaskFilterRequestDto filterDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return kanbanService.getFilteredBacklogTasks(filterDto, page, size);
    }
}