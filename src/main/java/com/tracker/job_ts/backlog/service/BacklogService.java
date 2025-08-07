package com.tracker.job_ts.backlog.service;

import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.backlog.dto.BacklogTaskFilterRequestDto;
import com.tracker.job_ts.base.model.PagedResult;
import com.tracker.job_ts.project.exception.ProjectNotFoundException;
import com.tracker.job_ts.project.repository.ProjectUserRepository;
import com.tracker.job_ts.projectTask.dto.ProjectTaskDto;
import com.tracker.job_ts.projectTask.entity.ProjectTask;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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

        query.addCriteria(Criteria.where("sprint").exists(false));


        if (filterDto.getProjectId().isPresent() &&
                !CollectionUtils.isEmpty(userProjectIds) &&
                userProjectIds.contains(filterDto.getProjectId().get()) ) {
            query.addCriteria(Criteria.where("createdProject.id").is(filterDto.getProjectId().get()));
        } else if (!CollectionUtils.isEmpty(userProjectIds)) {
            query.addCriteria(Criteria.where("createdProject.id").in(userProjectIds));
        }


        filterDto.getTaskType().ifPresent(taskType ->
                query.addCriteria(Criteria.where("taskType").is(taskType))
        );

        filterDto.getAssigneeId().ifPresent(assigneeId ->
                query.addCriteria(Criteria.where("assignee.id").is(assigneeId))
        );

// Yeni searchText filtresi
        String searchText = filterDto.getSearchText();
        if (StringUtils.hasText(searchText)) {
            // $or operatörü ile iki farklı regex koşulunu birleştir
            query.addCriteria(new Criteria().orOperator(
                    // 1. Koşul: Title sadece "elma" metnine eşitse
                    Criteria.where("title").regex("^" + searchText + "$", "i"),
                    // 2. Koşul: Title içinde "elma" metni herhangi bir yerde geçiyorsa
                    Criteria.where("title").regex(searchText, "i"),
                    // 3. Koşul: TaskNumber içinde "elma" metni herhangi bir yerde geçiyorsa
                    Criteria.where("taskNumber").regex("^" + searchText + "$", "i"),
                    // 2. Koşul: Title içinde "elma" metni herhangi bir yerde geçiyorsa
                    Criteria.where("taskNumber").regex(searchText, "i")
            ));
        }

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