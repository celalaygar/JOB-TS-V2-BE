package com.tracker.job_ts.WeeklyBoard.dto;

import com.tracker.job_ts.WeeklyBoard.entity.BoardDay;
import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyBoardResponseDto {
    private String id;
    private String title;
    private String description;
    private CreatedProject createdProject;
    private CreatedBy createdBy;
    private BoardDay day; // DayOfWeek yerine BoardDay kullanıldı
    private LocalTime time;
    private LocalDate date;
    private Boolean markAsCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}