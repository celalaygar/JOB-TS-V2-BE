package com.tracker.job_ts.sprint.dto;

import com.tracker.job_ts.project.model.CreatedBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

// SprintUser Oluşturma/Güncelleme İsteği DTO'su
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SprintUserRequestDto {
    private String sprintId;
    private String projectId;
    private String userId; // Sadece ID yeterli, servis katmanında CreatedBy'ye dönüştürülecek
    private String roleInSprint;
    private String statusInSprint;
    private Integer estimatedEffort;
    private String notes;
}
