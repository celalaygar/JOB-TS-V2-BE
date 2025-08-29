package com.tracker.job_ts.WeeklyBoard.entity;

import com.tracker.job_ts.project.model.CreatedBy;
import com.tracker.job_ts.project.model.CreatedProject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Document(collection = "weekly_board")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyBoard {
    @Id
    private String id;
    private String title;
    private String description;
    private CreatedProject createdProject; // Project info (can be null)
    private CreatedBy createdBy; // User who created the entry
    private BoardDay day; // DayOfWeek yerine BoardDay kullanıldı
    private LocalTime time; // Time of the day
    private LocalDate date; // Date (day, month, year)
    private Boolean markAsCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}