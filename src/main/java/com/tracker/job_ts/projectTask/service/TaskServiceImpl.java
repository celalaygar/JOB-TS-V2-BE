package com.tracker.job_ts.projectTask.service;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import com.tracker.job_ts.projectTask.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Mono<ProjectTask> createTask(ProjectTask task) {
        return taskRepository.save(task);
    }

    @Override
    public Mono<ProjectTask> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    @Override
    public Flux<ProjectTask> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Mono<ProjectTask> updateTask(String id, ProjectTask task) {
        return taskRepository.findById(id)
                .flatMap(existing -> {
                    task.setId(existing.getId());
                    return taskRepository.save(task);
                });
    }

    @Override
    public Mono<Void> deleteTask(String id) {
        return taskRepository.deleteById(id);
    }
}
