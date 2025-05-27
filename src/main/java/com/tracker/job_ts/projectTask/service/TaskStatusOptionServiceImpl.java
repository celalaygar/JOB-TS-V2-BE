package com.tracker.job_ts.projectTask.service;

import com.tracker.job_ts.project.exception.ProjectTaskStatusNotFoundException;
import com.tracker.job_ts.project.repository.ProjectTaskStatusRepository;
import com.tracker.job_ts.projectTask.dto.TaskStatusOptionDTO;
import com.tracker.job_ts.projectTask.entity.TaskStatusOption;
import com.tracker.job_ts.projectTask.exception.TaskStatusOptionNotFoundException;
import com.tracker.job_ts.projectTask.mapper.TaskStatusOptionMapper;
import com.tracker.job_ts.projectTask.repository.TaskStatusOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TaskStatusOptionServiceImpl implements TaskStatusOptionService {

    private final TaskStatusOptionRepository repository;
    private final ProjectTaskStatusRepository projectTaskStatusRepository;
    private final TaskStatusOptionMapper mapper;

    @Override
    public Mono<TaskStatusOptionDTO> create(TaskStatusOptionDTO dto) {
        return projectTaskStatusRepository.findById(dto.getProjectTaskStatusId())
                .switchIfEmpty(Mono.error(new ProjectTaskStatusNotFoundException("ProjectTaskStatus not found with id: " + dto.getProjectTaskStatusId())))
                .flatMap(pt -> repository.save(mapper.toEntity(dto)))
                .map(mapper::toDto);
    }

    @Override
    public Mono<TaskStatusOptionDTO> update(String id, TaskStatusOptionDTO dto) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new TaskStatusOptionNotFoundException("TaskStatusOption not found with id: " + id)))
                .flatMap(existing -> projectTaskStatusRepository.findById(dto.getProjectTaskStatusId())
                        .switchIfEmpty(Mono.error(new ProjectTaskStatusNotFoundException("ProjectTaskStatus not found with id: " + dto.getProjectTaskStatusId())))
                        .flatMap(project -> {
                            TaskStatusOption updated = mapper.toEntity(dto);
                            updated.setId(id);
                            return repository.save(updated);
                        }))
                .map(mapper::toDto);
    }

    @Override
    public Mono<TaskStatusOptionDTO> getById(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new TaskStatusOptionNotFoundException("TaskStatusOption not found with id: " + id)))
                .map(mapper::toDto);
    }

    @Override
    public Flux<TaskStatusOptionDTO> getByProjectTaskStatusId(String projectTaskStatusId) {
        return projectTaskStatusRepository.findById(projectTaskStatusId)
                .switchIfEmpty(Mono.error(new ProjectTaskStatusNotFoundException("ProjectTaskStatus not found with id: " + projectTaskStatusId)))
                .flatMapMany(p -> repository.findByProjectTaskStatusId(projectTaskStatusId))
                .map(mapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new TaskStatusOptionNotFoundException("TaskStatusOption not found with id: " + id)))
                .flatMap(existing -> repository.deleteById(id));
    }
}
