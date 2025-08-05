package com.tracker.job_ts.backlog.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.backlog.dto.BacklogTaskFilterRequestDto;
import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BacklogService {

    private final ProjectUserRepository projectUserRepository;
    private final AuthHelperService authHelperService;
    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<PagedResult<ProjectTaskDto>> getFilteredBacklogTasks(
            BacklogTaskFilterRequestDto filterDto, int page, int size) {

        return authHelperService.getAuthUser()
                .flatMap(user -> projectUserRepository.findAllByUserId(user.getId())
                        .map(projectUser -> projectUser.getProjectId())
                        .collectList()
                        .flatMap(projectIds -> {
                            if (projectIds.isEmpty()) {
                                return Mono.error(new NoSuchElementException("User is not a member of any project."));
                            }
                            return filterAndPaginateBacklogTasks(projectIds, filterDto, page, size);
                        })
                );
    }

    private Mono<PagedResult<ProjectTaskDto>> filterAndPaginateBacklogTasks(
            List<String> userProjectIds, BacklogTaskFilterRequestDto filterDto, int page, int size) {

        Query query = new Query();

        query.addCriteria(Criteria.where("createdProject.id").in(userProjectIds));
        query.addCriteria(Criteria.where("sprint").exists(false));

        filterDto.getProjectId().ifPresent(projectId -> {
            if (!userProjectIds.contains(projectId)) {
                throw new ProjectNotFoundException("You are not authorized to view tasks for this project.");
            }
            query.addCriteria(Criteria.where("createdProject.id").is(projectId));
        });

        filterDto.getTaskType().ifPresent(taskType ->
                query.addCriteria(Criteria.where("taskType").is(taskType))
        );

        filterDto.getAssigneeId().ifPresent(assigneeId ->
                query.addCriteria(Criteria.where("assignee.id").is(assigneeId))
        );

        // Yeni searchText filtresi
        filterDto.getSearchText().ifPresent(text -> {
            if (StringUtils.hasText(text)) {
                // $or operatörü ile title veya taskNumber alanında arama yap
                query.addCriteria(new Criteria().orOperator(
                        Criteria.where("title").regex(text, "i"), // case-insensitive arama
                        Criteria.where("taskNumber").regex(text, "i") // case-insensitive arama
                ));
            }
        });

        PageRequest pageRequest = PageRequest.of(page, size);
        query.with(pageRequest);

        Mono<List<ProjectTaskDto>> tasks = mongoTemplate.find(query, ProjectTask.class)
                .map(ProjectTaskDto::new)
                .collectList();

        Mono<Long> count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), ProjectTask.class);

        return Mono.zip(tasks, count)
                .map(tuple -> new PagedResult<>(tuple.getT1(), tuple.getT2(), page, size));
    }
}