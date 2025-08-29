package com.tracker.job_ts.WeeklyBoard.dto;

import com.tracker.job_ts.WeeklyBoard.entity.BoardDay;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WeeklyBoardRequestDto {
    @NotBlank(message = "Title cannot be blank.")
    private String title;
    private String description;
    private String projectId; // Not required
    @NotNull(message = "Day cannot be null.")
    private BoardDay day; // DayOfWeek yerine BoardDay kullanıldı
    @NotNull(message = "Time cannot be null.")
    private LocalTime time;
    @NotNull(message = "Date cannot be null.")
    private LocalDate date;
    private Boolean markAsCompleted = false;
}