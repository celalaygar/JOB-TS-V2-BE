package com.tracker.job_ts.sprint.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Kullanıcıları toplu ekleme/çıkartma için DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintTeamRequestDto {
    private String sprintId;
    private String projectId;
    private String projectTeamId; // Eklenecek/Çıkartılacak takımın ID'si
    private String roleInSprint; // Takımdaki kullanıcılara atanacak varsayılan rol
    private String statusInSprint; // Takımdaki kullanıcılara atanacak varsayılan durum
}
