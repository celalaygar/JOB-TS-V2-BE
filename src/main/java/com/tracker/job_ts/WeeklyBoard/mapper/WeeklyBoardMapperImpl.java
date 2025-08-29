package com.tracker.job_ts.WeeklyBoard.mapper;

import com.tracker.job_ts.WeeklyBoard.dto.WeeklyBoardRequestDto;
import com.tracker.job_ts.WeeklyBoard.dto.WeeklyBoardResponseDto;
import com.tracker.job_ts.WeeklyBoard.entity.WeeklyBoard;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Component
public class WeeklyBoardMapperImpl   {

    public Mono<WeeklyBoard> toEntity(WeeklyBoardRequestDto dto, CreatedBy createdBy, CreatedProject createdProject) {
        WeeklyBoard entity = WeeklyBoard.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .createdProject(createdProject)
                .day(dto.getDay())
                .time(dto.getTime())
                .date(dto.getDate())
                .markAsCompleted(dto.getMarkAsCompleted())
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return Mono.just(entity);
    }

    public WeeklyBoardResponseDto toDto(WeeklyBoard entity) {
        return new WeeklyBoardResponseDto(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCreatedProject(),
                entity.getCreatedBy(),
                entity.getDay(),
                entity.getTime(),
                entity.getDate(),
                entity.getMarkAsCompleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}