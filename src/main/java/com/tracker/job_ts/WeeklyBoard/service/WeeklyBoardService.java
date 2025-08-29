package com.tracker.job_ts.WeeklyBoard.service;


import com.tracker.job_ts.WeeklyBoard.dto.WeeklyBoardRequestDto;
import com.tracker.job_ts.WeeklyBoard.dto.WeeklyBoardResponseDto;
import com.tracker.job_ts.WeeklyBoard.entity.WeeklyBoard;
import com.tracker.job_ts.WeeklyBoard.mapper.WeeklyBoardMapperImpl;
import com.tracker.job_ts.WeeklyBoard.repository.WeeklyBoardRepository;
import com.tracker.job_ts.auth.service.AuthHelperService;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import com.tracker.job_ts.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class WeeklyBoardService {

    private final WeeklyBoardRepository weeklyBoardRepository;
    private final ProjectRepository projectRepository;
    private final AuthHelperService authHelperService;
    private final WeeklyBoardValidator validator;
    private final WeeklyBoardMapperImpl mapper;

    // Create Operation
    public Mono<WeeklyBoardResponseDto> create(WeeklyBoardRequestDto dto) {
        return validator.validate(dto)
                .flatMap(validatedDto -> authHelperService.getAuthUser()
                        .flatMap(authUser -> {
                            Mono<CreatedProject> projectMono = Mono.justOrEmpty(dto.getProjectId())
                                    .flatMap(projectId -> projectRepository.findById(projectId)
                                            .map(CreatedProject::new)
                                            .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found."))));

                            return projectMono.defaultIfEmpty(null)
                                    .flatMap(createdProject -> {
                                        CreatedBy createdBy = new CreatedBy(authUser);
                                        return mapper.toEntity(dto, createdBy, createdProject)
                                                .flatMap(weeklyBoardRepository::save)
                                                .map(mapper::toDto);
                                    });
                        })
                );
    }

    // Update Operation
    public Mono<WeeklyBoardResponseDto> update(String id, WeeklyBoardRequestDto dto) {
        return validator.validate(dto)
                .flatMap(validatedDto -> authHelperService.getAuthUser()
                        .flatMap(authUser -> weeklyBoardRepository.findByIdAndCreatedByUserId(id, authUser.getId())
                                .switchIfEmpty(Mono.error(new IllegalAccessException("You are not authorized to update this entry or it does not exist.")))
                                .flatMap(existingEntry -> {
                                    if (existingEntry.getDate().isBefore(LocalDate.now())) {
                                        return Mono.error(new IllegalAccessException("Cannot update entries for past dates."));
                                    }

                                    Mono<CreatedProject> projectMono = Mono.justOrEmpty(dto.getProjectId())
                                            .flatMap(projectId -> projectRepository.findById(projectId)
                                                    .map(CreatedProject::new)
                                                    .switchIfEmpty(Mono.error(new NoSuchElementException("Project not found."))));

                                    return projectMono.defaultIfEmpty(null)
                                            .flatMap(createdProject -> {
                                                existingEntry.setTitle(dto.getTitle());
                                                existingEntry.setDescription(dto.getDescription());
                                                existingEntry.setCreatedProject(createdProject);
                                                existingEntry.setDay(dto.getDay());
                                                existingEntry.setTime(dto.getTime());
                                                existingEntry.setDate(dto.getDate());
                                                existingEntry.setMarkAsCompleted(dto.getMarkAsCompleted());
                                                existingEntry.setUpdatedAt(LocalDateTime.now());

                                                return weeklyBoardRepository.save(existingEntry)
                                                        .map(mapper::toDto);
                                            });
                                })
                        )
                );
    }

    // Delete Operation
    public Mono<Void> delete(String id) {
        return authHelperService.getAuthUser()
                .flatMap(authUser -> weeklyBoardRepository.findByIdAndCreatedByUserId(id, authUser.getId())
                        .switchIfEmpty(Mono.error(new IllegalAccessException("You are not authorized to delete this entry or it does not exist.")))
                        .flatMap(existingEntry -> {
                            if (existingEntry.getDate().isBefore(LocalDate.now())) {
                                return Mono.error(new IllegalAccessException("Cannot delete entries for past dates."));
                            }
                            return weeklyBoardRepository.delete(existingEntry);
                        })
                );
    }

    public Flux<WeeklyBoardResponseDto> getWeeklyBoardData(int year, int month, int week) {
        return authHelperService.getAuthUser()
                .flatMapMany(authUser -> {
                    // Yıl ve ayın ilk gününü al
                    LocalDate firstDayOfMonth = LocalDate.of(year, month, 1);

                    // Haftanın başlangıcını hesapla
                    LocalDate startDateOfWeek = firstDayOfMonth
                            .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY))
                            .plusWeeks(week - 1);

                    // Haftanın sonunu hesapla
                    LocalDate endDateOfWeek = startDateOfWeek.plusDays(6);

                    return weeklyBoardRepository.findByCreatedByUserIdAndDateBetweenOrderByDateAscTimeAsc(
                                    authUser.getId(), startDateOfWeek, endDateOfWeek)
                            .map(mapper::toDto);
                });
    }
}